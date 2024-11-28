package prod.prog

import com.bennyhuo.kotlin.analyzer.KotlinCodeAnalyzer
import com.bennyhuo.kotlin.analyzer.buildOptions
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.util.isOrdinaryClass

class CodeAnalyzer(paths: List<String>) {
    private val codeAnalysis = KotlinCodeAnalyzer(buildOptions {
        inputPaths = paths
        // can't see an option not to print errors
        // probably it's somewhere inside the inner compiler library
    }).analyze()

    val allFileNames =
        codeAnalysis.files.map { ktFile ->
            val packageName = ktFile.packageFqName
            val fileName = ktFile.name.substringBeforeLast(".").substringAfterLast("\\")
            "$packageName.$fileName"
        }.toSet()

    private val allImports =
        codeAnalysis.files.associate { ktFile ->
            val packageName = ktFile.packageFqName
            val fileName = ktFile.name.substringBeforeLast(".").substringAfterLast("\\")
            val fullFilename = "$packageName.$fileName"

            val imports: MutableSet<String> = mutableSetOf()
            ktFile.importDirectives.forEach { ktImportDirective ->
                ktImportDirective.importPath?.fqName?.asString()?.let { importName ->
                    imports.add(importName)
                }
            }
            fullFilename to imports
        }

    var fileToModuleMap = allFileNames.associateWith { it }

    private fun fileDependencies(): Map<String, Set<String>> {
        // finding all imported files for each wildcard import
        val filesByModule = mutableMapOf<String, Set<String>>()
        allImports.keys.forEach { filePath ->
            val filePathParts = filePath.split(".")
            filePathParts.indices.forEach { i ->
                val module = filePathParts.take(i + 1).joinToString(".")
                filesByModule[module] = filesByModule.getOrDefault(module, mutableSetOf()) + filePath
            }
        }

        // resolving wildcard imports
        // removing imports of external code
        // removing import of itself (can occur with wildcard)
        return allImports.mapValues { (file, imports) ->
            imports
                .flatMap { import ->
                    var newImport = import
                    var result: Set<String>? = null
                    // sometimes import goes to inner class or function in file
                    while (result == null) {
                        result = filesByModule[newImport]
                        if (!newImport.contains("."))
                            break
                        newImport = newImport.substringBeforeLast(".")
                        // we do not seek files in our ancestors
                        if (file.startsWith(newImport))
                            break
                    }
                    // setOf("EXTERNAL") to see all imports from external code
                    result ?: setOf()
                }
                .toSet() - file
        }
    }

    fun moduleDependencies() = moduleDependencies(
        fileDependencies(),
        fileToModuleMap
    )

    fun instabilityRate() = moduleDependencies().mapValues { (name, dependencies) ->
            val efferentImports = dependencies.sumOf { it.second }
            val afferentImports = moduleDependencies().values.flatten().sumOf {
                if (it.first == name)
                    it.second
                else
                    0
            }
            efferentImports.toDouble() / (efferentImports + afferentImports)
        }

    fun abstractionRates() = moduleAbstractionRates(
        fileAbstractionRates(),
        fileToModuleMap
    )

    private fun fileAbstractionRates(): Map<String, Double> =
        codeAnalysis.files.associate { ktFile ->
            val packageName = ktFile.packageFqName
            val fileName = ktFile.name.substringBeforeLast(".").substringAfterLast("\\")
            val moduleName = "$packageName.$fileName"

            var abstractionRate = 0.0
            var multiplier = 0.0
            ktFile.declarations.map { ktDeclaration ->
                multiplier += 1.0
                abstractionRate += if (ktDeclaration !is KtClass) {
                    // some function (probably)
                    0.0
                } else {
                    when {
                        ktDeclaration.isAbstract() -> 1.0
                        else -> 0.0
                    }
                }
            }
            Pair(moduleName, abstractionRate / multiplier)
        }

}