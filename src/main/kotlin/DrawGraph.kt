package prod.prog

import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.engine.Engine
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.toGraphviz
import java.io.File
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

fun colorByString(string: String): Color {
    val hash = string.hashCode()
    val red = (hash shr 16) and 0xFF
    val green = (hash shr 8) and 0xFF
    val blue = hash and 0xFF
    return Color.rgb(
        max(255, red * red),
        max(255, green * green),
        max(255, blue * blue)
    )
}

fun generateDotFile(graph: Map<String, Set<Pair<String, Int>>>, filePath: String) {
    guru.nidi.graphviz.graph(directed = true, name = "Class Dependencies") {
        val edgeWeights =
            graph.values
                .flatten()
                .map { it.second }

        val maxWeight = log10(edgeWeights.max().toDouble())
        val minWeight = log10(edgeWeights.min().toDouble())
        val maxColor = 205
        val minColor = 0

        for ((vertex, neighbors) in graph)
            for ((neighbor, weight) in neighbors) {
                val weightDouble = (log10(weight.toDouble()) - minWeight) / (maxWeight - minWeight)
                val color =
                    min(
                        255, max(
                            0,
                            (maxColor + minColor - weightDouble * (maxColor - minColor)).toInt()
                        )
                    )
                (vertex - neighbor)[
                    Color.rgb(color, color, color),
                    Label.of(weight.toString()),
                ]
            }

        for (vertex in graph.keys)
            vertex[colorByString(vertex.substringBeforeLast("."))]

    }.toGraphviz()
        // Engine.FDP looks good too
        .engine(Engine.DOT)
        .render(Format.PNG)
        .toFile(File(filePath))
}