package prod.prog

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.points

fun drawAbstractionInstability(instabilityRate: Map<String, Double>, abstractionRate: Map<String, Double>) {
    val moduleNames = instabilityRate.keys.toList()
    val data = dataFrameOf(
        "moduleName" to moduleNames,
        "instabilityRate" to moduleNames.map { instabilityRate[it]!! },
        "abstractionRate" to moduleNames.map { abstractionRate[it]!! }
    )

    plot(data) {
        points {
            x("instabilityRate")
            y("abstractionRate")
            color("moduleName")
        }
    }.save("abstractionInstability.png", path = "results/")
}