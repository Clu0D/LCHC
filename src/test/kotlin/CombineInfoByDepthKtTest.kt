import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import prod.prog.combineToModulesByDepth

class CombineInfoByDepthKtTest : StringSpec({
    val dependencies = mapOf(
        "testFiles.a.A" to setOf("testFiles.B", "testFiles.c.d.C", "testFiles.a.D"),
        "testFiles.a.D" to setOf("testFiles.B", "testFiles.c.d.C"),
        "testFiles.B" to setOf(),
        "testFiles.c.d.C" to setOf(),
    )

    "combineToModulesByDepth(0 5 true)" {
        val names = combineToModulesByDepth(dependencies.keys, 0, 5, true)
        names["testFiles.a.A"]!! shouldBeEqual ""
        names["testFiles.a.D"]!! shouldBeEqual ""
        names["testFiles.B"]!! shouldBeEqual ""
        names["testFiles.c.d.C"]!! shouldBeEqual ""
    }

    "combineToModulesByDepth(0 0 false) changes nothing" {
        val names = combineToModulesByDepth(dependencies.keys, 0, 0, false)
        names["testFiles.a.A"]!! shouldBeEqual "testFiles.a.A"
        names["testFiles.a.D"]!! shouldBeEqual "testFiles.a.D"
        names["testFiles.B"]!! shouldBeEqual "testFiles.B"
        names["testFiles.c.d.C"]!! shouldBeEqual "testFiles.c.d.C"
    }

    "combineToModulesByDepth(0 1 false) does not remove things" {
        val names = combineToModulesByDepth(dependencies.keys, 0, 1, false)
        names["testFiles.a.A"]!! shouldBeEqual "a.A"
        names["testFiles.a.D"]!! shouldBeEqual "a.D"
        names["testFiles.B"]!! shouldBeEqual "B"
        names["testFiles.c.d.C"]!! shouldBeEqual "c.d.C"
    }

    "combineToModulesByDepth(0 2 false) does not combine modules" {
        val names = combineToModulesByDepth(dependencies.keys, 0, 2, false)
        names["testFiles.a.A"]!! shouldBeEqual "A"
        names["testFiles.a.D"]!! shouldBeEqual "D"
        names["testFiles.B"]!! shouldBeEqual "B"
        names["testFiles.c.d.C"]!! shouldBeEqual "d.C"
    }

    "combineToModulesByDepth(3 2 false) does combine modules" {
        val names = combineToModulesByDepth(dependencies.keys, 3, 2, false)
        names["testFiles.a.A"]!! shouldBeEqual "A"
        names["testFiles.a.D"]!! shouldBeEqual "D"
        names["testFiles.B"]!! shouldBeEqual "B"
        names["testFiles.c.d.C"]!! shouldBeEqual "d"
    }

    "combineToModulesByDepth(2 2 false) does combine modules" {
        val names = combineToModulesByDepth(dependencies.keys, 2, 2, false)
        names["testFiles.a.A"]!! shouldBeEqual "a"
        names["testFiles.a.D"]!! shouldBeEqual "a"
        names["testFiles.B"]!! shouldBeEqual "B"
        names["testFiles.c.d.C"]!! shouldBeEqual "c"
    }
})
