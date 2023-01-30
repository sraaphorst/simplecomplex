package math

import kotlin.math.absoluteValue

const val DEFAULT_PRECISION = 1e-5

fun <S : Number, T : Number> almostEquals(x: S,
                                          y: T,
                                          precision: Double = DEFAULT_PRECISION): Boolean =
    (x.toDouble() - y.toDouble()).absoluteValue < precision

fun <S : ComplexBase, T: ComplexBase> almostEquals(x: S, y: T, precision: Double = DEFAULT_PRECISION): Boolean =
    almostEquals(x.toComplex.re, y.toComplex.re) && almostEquals(x.toComplex.im, y.toComplex.im)
