package math

import kotlin.math.*

sealed interface ComplexBase {
    val real: Boolean
    val imag: Boolean
    val magnitude: Double
    val toPolar: Polar
    val toCartesian: Cartesian
}

class Polar(val r: Double, thetaUnbounded: Double): ComplexBase {
    constructor(r: Number, thetaAny: Number): this(r.toDouble(), thetaAny.toDouble())

    val theta = fixAngle(thetaUnbounded)

    override val real: Boolean
        get() = Compare.almostEquals(0.0, r * cos(theta))

    override val imag: Boolean
        get() = Compare.almostEquals(0.0, r * sin(theta))

    val conjugate: Polar
        get() = Polar(r, -theta)

    override val magnitude: Double
        get() = r

    override val toPolar: Polar = this

    override val toCartesian: Cartesian
        get() = Cartesian(r * cos(theta), r * sin(theta))

    fun pow(n: Number): Polar =
        Polar(r.pow(n.toDouble()), n.toDouble() * theta)

    operator fun unaryMinus(): Polar =
        Polar(-r, theta)

    operator fun times(other: Polar): Polar =
        Polar(r * other.r, theta + other.theta)

    operator fun div(other: Polar): Polar =
        Polar(r / other.r, theta - other.theta)

    override fun toString(): String =
        "Polar(r=$r, theta=$theta)"

    companion object {
        // Zero does not have a unique representation in polar coordinates.
        // Anything with r = 0 is zero.
        val ZERO = Polar(0, 0)
        val ONE = Polar(1, PI)
        val I = Polar(1, PI / 2)

        tailrec fun fixAngle(angle: Double): Double {
            return if (angle >= 0 && angle < 2 * PI) angle
            else if (angle < 0) fixAngle(angle + 2 * PI)
            else fixAngle(angle - 2 * PI)
        }
    }
}

data class Cartesian(val re: Double, val im: Double): ComplexBase {
    constructor(re: Number, im: Number): this(re.toDouble(), im.toDouble())

    override val real: Boolean
        get() = Compare.almostEquals(0.0, im)

    override val imag: Boolean
        get() = Compare.almostEquals(0.0, re)

    val conjugate: Cartesian
        get() = Cartesian(re, -im)

    override val magnitude: Double
        get() = sqrt(re * re + im * im)

    override val toPolar: Polar
        get() {
            return Polar(magnitude, atan2(im, re))
        }

    override val toCartesian: Cartesian = this

    operator fun unaryMinus(): Cartesian =
        Cartesian(-re, -im)

    operator fun plus(other: Cartesian): Cartesian =
        Cartesian(re + other.re, im + other.im)

    operator fun plus(other: Number): Cartesian =
        Cartesian(re + other.toDouble(), im)

    operator fun minus(other: Cartesian): Cartesian =
        Cartesian(re - other.re, im - other.im)

    operator fun minus(other: Number): Cartesian =
        Cartesian(re - other.toDouble(), im)

    operator fun times(other: Cartesian): Cartesian =
        Cartesian(re * other.re - im * other.im, re * other.im + im * other.re)

    operator fun times(other: Number): Cartesian =
        Cartesian(re * other.toDouble(), im * other.toDouble())

    operator fun div(other: Cartesian): Cartesian =
        Cartesian(
            (re * other.re + im * other.im) / (other.re * other.re + other.im * other.im),
            (im * other.re - re * other.im) / (other.re * other.re + other.im * other.im)
        )

    operator fun div(other: Number): Cartesian =
        Cartesian(re / other.toDouble(), im / other.toDouble())

    // To do power, easier to change into polar coordinates and then change back.
    fun ppow(n: Number): Cartesian =
        toPolar.pow(n).toCartesian

    // If the user insists, can do via Complex, but only to non-negative Int values.
    fun ipow(n: Int): Cartesian =
        if (n < 0) throw ArithmeticException("Cannot evaluate $this.ipow($n): use ppow($n) instead.")
        else if (n == 0) Cartesian(1, 0)
        else this * ipow(n - 1)

    companion object {
        val ZERO = Cartesian(0, 0)
        val ONE = Cartesian(1, 0)
        val I = Cartesian(0, 1)
    }
}

val Number.I: Cartesian
    get() = Cartesian(0.0, toDouble())

operator fun Number.plus(cartesian: Cartesian): Cartesian =
    Cartesian(toDouble() + cartesian.re, cartesian.im)

operator fun Number.minus(cartesian: Cartesian): Cartesian =
    Cartesian(toDouble() - cartesian.re, -cartesian.im)

operator fun Number.times(cartesian: Cartesian): Cartesian =
    Cartesian(toDouble() * cartesian.re, toDouble() * cartesian.im)

operator fun Number.div(cartesian: Cartesian): Cartesian =
    Cartesian(
        toDouble() * cartesian.re / (cartesian.re * cartesian.re + cartesian.im * cartesian.im),
        (- toDouble() * cartesian.im) / (cartesian.re * cartesian.re + cartesian.im * cartesian.im)
    )

object Compare {
    internal const val DEFAULT_PRECISION = 1e-5

    fun <S : Number, T : Number> almostEquals(
        x: S,
        y: T,
        precision: Double = DEFAULT_PRECISION
    ): Boolean =
        (x.toDouble() - y.toDouble()).absoluteValue < precision

    fun <S : ComplexBase, T : ComplexBase> almostEquals(x: S, y: T, precision: Double = DEFAULT_PRECISION): Boolean =
        almostEquals(x.toCartesian.re, y.toCartesian.re) && almostEquals(x.toCartesian.im, y.toCartesian.im)
}
