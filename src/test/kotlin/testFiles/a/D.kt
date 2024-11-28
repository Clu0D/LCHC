package testFiles.a

import testFiles.B
import testFiles.c.d.C
import kotlin.math.max

class D {
    val length = max(B().string.length, C().anotherString.length)
}