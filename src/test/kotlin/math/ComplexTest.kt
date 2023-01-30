package math

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.pair
import kotlin.math.PI

val complexArb = arbitrary {
    val re = Arb.numericDouble(-1e3, 1e3).bind().toDouble()
    val im = Arb.numericDouble(-1e3, 1e3).bind().toDouble()
    Complex(re, im)
}

val polarArb = arbitrary {
    val r = Arb.numericDouble(-1e3, 1e3).bind().toDouble()
    val theta = Arb.numericDouble(0.0, 2 * PI).bind().toDouble()
    Polar(r, theta)
}

class ComplexTest: StringSpec({
    "Using I suffix on a number creates an imaginary number" {
        forAll(Arb.int(-1000, 1000)) { n ->
            n.I == Complex(0, n)
        }
    }

    "Inversion of complex number times itself is 1" {
        forAll(complexArb) { c ->
            // We do not want numbers extremely close to 0.
            almostEquals(Complex.ZERO, c) || almostEquals(Complex.ONE, c * (1 / c))
        }
    }

    "Multiplying by complex conjugate gives real" {
        forAll(complexArb) { c ->
            val value = (c.conjugate * c)
            almostEquals(0.0, value.im)
        }
    }

    "Conjugate is self inverting" {
        forAll(complexArb) { c ->
            almostEquals(c, c.conjugate.conjugate)
        }
    }

    "Multiplying by complex conjugate gives square of magnitude" {
        forAll(complexArb) { c ->
            val value = (c.conjugate * c)
            almostEquals(c.magnitude * c.magnitude, value.re)
            almostEquals(0, value.im)
        }
    }

    "Multiplying two numbers in polar coordinates gives same result as multiplying in cartesian coordinates" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            almostEquals((p1 * p2).toComplex, (p1.toComplex * p2.toComplex))
        }
    }
})
