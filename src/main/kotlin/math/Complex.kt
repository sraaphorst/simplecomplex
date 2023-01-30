package math

import kotlin.math.*

sealed interface ComplexBase {
    val toPolar: Polar
    val toComplex: Complex
}

data class Polar(val r: Double, val theta: Double): ComplexBase {
    override val toPolar: Polar = this

    override val toComplex: Complex
        get() = Complex(r * cos(theta), r * sin(theta))

    fun pow(n: Number): Polar =
        Polar(r.pow(n.toDouble()), theta.pow(n.toDouble()))

    operator fun times(other: Polar): Polar =
        Polar(r * other.r, theta + other.theta)
}

data class Complex(val re: Double, val im: Double): ComplexBase {
    constructor(re: Number, im: Number): this(re.toDouble(), im.toDouble())

    val real: Boolean
        get() = almostEquals(0.0, im)
    val imag: Boolean
        get() = almostEquals(0.0, re)

    override val toPolar: Polar
        get() = Polar(magnitude, acos(re / magnitude))

    override val toComplex: Complex = this

    operator fun unaryMinus(): Complex =
        Complex(-re, -im)

    operator fun plus(other: Complex): Complex =
        Complex(re + other.re, im + other.im)

    operator fun plus(other: Number): Complex =
        Complex(re + other.toDouble(), im)

    operator fun minus(other: Complex): Complex =
        Complex(re - other.re, im - other.im)

    operator fun minus(other: Number): Complex =
        Complex(re - other.toDouble(), im)

    operator fun times(other: Complex): Complex =
        Complex(re * other.re - im * other.im, re * other.im + im * other.re)

    operator fun times(other: Number): Complex =
        Complex(re * other.toDouble(), im * other.toDouble())

    operator fun div(other: Complex): Complex =
        Complex(
            (re * other.re + im * other.im) / (other.re * other.re + other.im * other.im),
            (im * other.re - re * other.im) / (other.re * other.re + other.im * other.im)
        )

    operator fun div(other: Number): Complex =
        Complex(re / other.toDouble(), im / other.toDouble())

    // To do power, easier to change into polar coordinates and then change back.
    fun ppow(n: Number): Complex =
        toPolar.pow(n).toComplex

    // If the user insists, can do via Complex, but only to non-negative Int values.
    fun ipow(n: Int): Complex =
        if (n < 0) throw ArithmeticException("Cannot evaluate $this.ipow($n): use ppow($n) instead.")
        else if (n == 0) Complex(1, 0)
        else (this * this) * ipow(n - 1)

    val conjugate: Complex
        get() = Complex(re, -im)

    val magnitude: Double
        get() = sqrt(re * re + im * im)

    companion object {
        val ZERO = Complex(0, 0)
        val ONE = Complex(1, 0)
        val I = Complex(0, 1)
    }
}

val Number.I: Complex
    get() = Complex(0.0, toDouble())

operator fun Number.plus(complex: Complex): Complex =
    Complex(toDouble() + complex.re, complex.im)

operator fun Number.minus(complex: Complex): Complex =
    Complex(toDouble() - complex.re, -complex.im)

operator fun Number.times(complex: Complex): Complex =
    Complex(toDouble() * complex.re, toDouble() * complex.im)

operator fun Number.div(complex: Complex): Complex =
    Complex(
        toDouble() * complex.re / (complex.re * complex.re + complex.im * complex.im),
        (- toDouble() * complex.im) / (complex.re * complex.re + complex.im * complex.im)
    )

