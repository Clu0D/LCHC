package testFiles.a

import testFiles.*
import testFiles.c.d.C
import kotlin.math.max

class A {
    val length = max(B().string.length, C().anotherString.length)
}