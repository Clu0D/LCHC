package prod.prog

/**
 * Creates a Dependency graph and Abstraction Instability plot for modules
 *
 * files are saved in `results` folder (already contains example run results)
 *
 * [args] is a sequence of any number of threes:
 * 1. absolute path to directory with files
 * 2. depth for module creation (all files with package name of greater depth will be in one module)
 * 3. unused depth for removing same prefix from module names
 *
 * Example:
 *
 * [NewsSummarizer](https://github.com/Clu0D/NewsSummarizerBot) project structure contains
 * - prod.prog.service.logger.ConsoleLogger.kt
 * - prod.prog.service.Service.kt
 * - prod.prog.utils.MarkdownUtils.kt
 * - prod.prog.utils.XmlUtils.kt
 *
 * for run with [args] = ` ["D:\yourPath\NewsSummarizerBot\src\main\kotlin\service", "4", "2",
 * "D:\yourPath\NewsSummarizerBot\src\main\kotlin", "3", "2"]`
 *
 * analyzed modules will be named
 * - service.logger
 * - service.Service
 * - utils
 */
fun main(args: Array<String>) {
    var allDirectories = listOf<String>()
    var fileToModuleMap: Map<String, String> = mapOf()

    // reading filenames and cropping them by depth
    for (i in args.indices step 3) {
        val directory = args[i]
        allDirectories = allDirectories + directory
        val depth = args[i + 1].toInt()
        val unusedDepth = args[i + 2].toInt()
        val filenames = CodeAnalyzer(listOf(directory)).allFileNames
        fileToModuleMap = fileToModuleMap +
                (combineToModulesByDepth(filenames, depth, unusedDepth) - fileToModuleMap.keys)
    }

    // analyzing all the code, adding filename substitution dictionary
    val codeAnalyzer = CodeAnalyzer(allDirectories)
    codeAnalyzer.fileToModuleMap = fileToModuleMap

    generateDotFile(codeAnalyzer.moduleDependencies(), "results/moduleDependencies.png")

    drawAbstractionInstability(
        codeAnalyzer.instabilityRate(),
        codeAnalyzer.abstractionRates()
    )
}

