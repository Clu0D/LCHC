import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import prod.prog.CodeAnalyzer

class CodeAnalyzerTest : StringSpec({
    val pathToFile = this::class.java.getResource("/")!!.path
    assert(pathToFile.endsWith("/LCHC/build/classes/kotlin/test/")) {
        error(
            "ERROR!\n\t" +
                    "something gone wrong with test files auto import\n\t" +
                    "pathToTest should be absolute path to \"testFiles\" folder for tests to work\n"
        )
    }
    val pathToTests = pathToFile.substringBeforeLast("/build/") + "/src/test/kotlin/testFiles"

    val analyzer = CodeAnalyzer(listOf(pathToTests))
    val dependencies = analyzer.moduleDependencies()

    "imports with wildcard work" {
        dependencies.keys shouldContainExactlyInAnyOrder setOf(
            "testFiles.a.A",
            "testFiles.B",
            "testFiles.c.d.C",
            "testFiles.a.D"
        )
        dependencies["testFiles.a.A"]!!.map { it.first } shouldContainExactlyInAnyOrder listOf(
            "testFiles.B",
            "testFiles.c.d.C",
            "testFiles.a.D"
        )
        dependencies["testFiles.a.D"]!!.map { it.first } shouldContainExactlyInAnyOrder listOf(
            "testFiles.B",
            "testFiles.c.d.C"
        )
        dependencies["testFiles.B"]!!.map { it.first } shouldContainExactlyInAnyOrder listOf()
        dependencies["testFiles.c.d.C"]!!.map { it.first } shouldContainExactlyInAnyOrder listOf()
    }
})
