package prod.prog

fun moduleAbstractionRates(
    fileAbstractionRates: Map<String, Double>,
    fileToModuleMap: Map<String, String>
): Map<String, Double> = combineInfoWithFileToModuleMap(
    fileAbstractionRates,
    fileToModuleMap
) { _, info: List<Double> ->
    info.sum() / info.size.toDouble()
}

fun moduleDependencies(fileDependencies: Map<String, Set<String>>, fileToModuleMap: Map<String, String>) =
    combineInfoWithFileToModuleMap(
        fileDependencies,
        fileToModuleMap
    ) { name, info: List<Set<String>> ->
        info.flatten()
            .groupBy { fileToModuleMap[it] ?: "EXTERNAL_CODE" }
            .map { (name, list) -> Pair(name, list.size) }
            .filter { it.first != name }
            .toSet()
    }

fun <T, R> combineInfoWithFileToModuleMap(
    fileInfo: Map<String, T>,
    fileToModuleMap: Map<String, String>,
    combineInfoFunction: (String, List<T>) -> R
) = fileInfo.toList().map { (name, info) ->
    Pair(fileToModuleMap[name]!!, info)
}.groupBy { (name, _) ->
    name
}.mapValues { (name, info) ->
    combineInfoFunction(name, info.map { it.second })
}

/**
 * combines files to modules keeping [depth]-sized prefix
 *
 * [unusedDepth]-sized prefix is removed from module names.
 * The biggest common prefix is removed, if removing specified prefix is going to combine different modules.
 * Set [strictCrop] = true to disable this behavior.
 *
 * Ex: [depth] = 2, [unusedDepth] = 1, [strictCrop] = true
 * combines "main.module" and "test.module" in "module".
 *
 * [depth] = 0 is for infinite depth (results in file dependencies)
 *
 * returns file to modules map
 */
fun combineToModulesByDepth(
    files: Set<String>,
    depth: Int = 0,
    unusedDepth: Int = 0,
    strictCrop: Boolean = false
): Map<String, String> =
    files.associateWith { name ->
        var croppedName = name

        // crop back
        if (depth > 0)
            while (croppedName.count { it == '.' } >= depth)
                croppedName = croppedName.substringBeforeLast(".")

        // crop front
        repeat(unusedDepth) { iteration ->
            croppedName =
                if (strictCrop && !croppedName.contains("."))
                    ""
                else if (strictCrop)
                    croppedName.substringAfter(".")
                else {
                    val strictlyCroppedModules =
                        combineToModulesByDepth(files - croppedName, iteration, unusedDepth, true)

                    val newCroppedName = croppedName.substringAfter(".")
                    if (newCroppedName.isEmpty()
                        || strictlyCroppedModules.count { it.value == newCroppedName } > 1
                    )
                        croppedName
                    else
                        newCroppedName
                }
        }
        croppedName
    }