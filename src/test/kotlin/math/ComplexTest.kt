package math

// By Sebastian Raaphorst, 2023.

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import kotlin.math.PI

val doubleArb = Arb.numericDouble(-1e3, 1e3)
val angleArb = Arb.numericDouble(0.0, 2.0 * PI)

val cartesianArb = arbitrary {
    val re = doubleArb.bind().toDouble()
    val im = doubleArb.bind().toDouble()
    Cartesian(re, im)
}

val polarArb = arbitrary {
    val r = doubleArb.bind().toDouble()
    val theta = angleArb.bind().toDouble()
    Polar(r, theta)
}

class ComplexTest: StringSpec({
    "Cartesian: using I suffix on a number creates an imaginary element" {
        forAll(doubleArb) { n ->
            Compare.almostEquals(n.I, Cartesian(0, n))
        }
    }

    "Polar: using J suffix on a number creates an imaginary element" {
        forAll(doubleArb) { n ->
            Compare.almostEquals(n.J, Polar(n, PI / 2))
        }
    }

    "Cartesian: additive inverses exist" {
        forAll(cartesianArb) { c ->
            Compare.almostEquals(Cartesian.ZERO, c + (-c))
        }
    }

    "Cartesian: multiplicative inverses exist" {
        forAll(cartesianArb) { c ->
            Compare.almostEquals(Cartesian.ZERO, c) || Compare.almostEquals(Cartesian.ONE, c * (1 / c))
        }
    }

    "Polar: additive inverses exist" {
        forAll(polarArb) { p ->
            Compare.almostEquals(Polar.ZERO, p + (-p))
        }
    }

    "Polar: multiplicative inverses exist" {
        forAll(polarArb) { p ->
            Compare.almostEquals(Polar.ZERO, p) || Compare.almostEquals(Polar.ONE, p * (1 / p))
        }
    }

    "Cartesian: addition is commutative" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            Compare.almostEquals(c1 + c2, c2 + c1)
        }
    }

    "Cartesian: addition is associative" {
        forAll(Arb.triple(cartesianArb, cartesianArb, cartesianArb)) { (c1, c2, c3) ->
            Compare.almostEquals(c1 + (c2 + c3), (c1 + c2) + c3)
        }
    }

    "Cartesian: subtraction is equivalent to addition with additive inverses" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            c1 - c2 == c1 + (-c2)
        }
    }

    "Cartesian: multiplication is commutative" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            c1 * c2 == c2 * c1
        }
    }

    "Cartesian: multiplication is associative" {
        forAll(Arb.triple(cartesianArb, cartesianArb, cartesianArb)) { (c1, c2, c3) ->
            Compare.almostEquals(c1 * (c2 * c3), (c1 * c2) * c3)
        }
    }

    "Cartesian: multiplication by scalars is commutative" {
        forAll(Arb.pair(doubleArb, cartesianArb)) { (n, c) ->
            Compare.almostEquals(n * c, c * n)
        }
    }

    "Cartesian: multiplication by scalars is associative" {
        forAll(Arb.triple(doubleArb, doubleArb, cartesianArb)) { (n1, n2, c) ->
            Compare.almostEquals((n1 * n2) * c, n1 * (n2 * c))
        }
    }

    "Cartesian: division is equivalent to multiplication with multiplicative inverses" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            Compare.almostEquals(Cartesian.ZERO, c2) ||
                    Compare.almostEquals(c1 / c2, c1 * (1 / c2))
        }
    }

    "Polar: addition is commutative" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            Compare.almostEquals(p1 + p2, p2 + p1)
        }
    }

    "Polar: addition is associative" {
        forAll(Arb.triple(polarArb, polarArb, polarArb)) { (p1, p2, p3) ->
            Compare.almostEquals(p1 + (p2 + p3), (p1 + p2) + p3)
        }
    }

    "Polar: multiplication is commutative" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            Compare.almostEquals(p1 * p2, p2 * p1)
        }
    }

    "Polar: multiplication is associative" {
        forAll(Arb.triple(polarArb, polarArb, polarArb)) { (p1, p2, p3) ->
            Compare.almostEquals(p1 * (p2 * p3), (p1 * p2) * p3)
        }
    }

    "Polar: multiplication by scalars is commutative" {
        forAll(Arb.pair(doubleArb, polarArb)) { (n, p) ->
            Compare.almostEquals(n * p, p * n)
        }
    }

    "Polar: multiplication by scalars is associative" {
        forAll(Arb.triple(doubleArb, doubleArb, polarArb)) { (n1, n2, p) ->
            Compare.almostEquals((n1 * n2) * p, n1 * (n2 * p))
        }
    }

    "Polar: division is equivalent to multiplication with multiplicative inverses" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            Compare.almostEquals(Polar.ZERO, p2) ||
                    Compare.almostEquals(p1 / p2, p1 * (1 / p2))
        }
    }

    "Cartesian: multiplying by conjugate gives real" {
        forAll(cartesianArb) { c ->
            Compare.almostEquals(0, (c.conjugate * c).im)
        }
    }

    "Polar: multiplying by conjugate gives real" {
        forAll(polarArb) { p ->
            Compare.almostEquals(0, (p.conjugate * p).theta)
        }
    }

    "Cartesian: conjugate is self inverting" {
        forAll(cartesianArb) { c ->
            Compare.almostEquals(c, c.conjugate.conjugate)
        }
    }

    "Polar: conjugate is self inverting" {
        forAll(polarArb) { p ->
            Compare.almostEquals(p, p.conjugate.conjugate)
        }
    }

    "Cartesian: multiplying by conjugate gives square of magnitude" {
        forAll(cartesianArb) { c ->
            val value = (c.conjugate * c)
            Compare.almostEquals(c.magnitude * c.magnitude, value.re)
            Compare.almostEquals(0, value.im)
        }
    }

    "Polar: multiplying by conjugate gives square of magnitude" {
        forAll(polarArb) { p ->
            val value = (p.conjugate * p)
            Compare.almostEquals(p.magnitude * p.magnitude, value.r)
            Compare.almostEquals(0, value.theta)
        }
    }

    "Multiplying two numbers in polar coordinates gives same result as in cartesian coordinates" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            Compare.almostEquals((p1 * p2).toCartesian, (p1.toCartesian * p2.toCartesian))
        }
    }

    "Multiplying two numbers in cartesian coordinates gives same result as in polar coordinates" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            Compare.almostEquals((c1 * c2).toPolar, (c1.toPolar * c2.toPolar))
        }
    }

    "Dividing two numbers in polar coordinates gives same result as in cartesian coordinates" {
        forAll(Arb.pair(polarArb, polarArb)) { (p1, p2) ->
            Compare.almostEquals(0.0, p2.magnitude) ||
            Compare.almostEquals((p1 / p2).toCartesian, (p1.toCartesian / p2.toCartesian))
        }
    }

    "Dividing two numbers in cartesian coordinates gives same result as in polar coordinates" {
        forAll(Arb.pair(cartesianArb, cartesianArb)) { (c1, c2) ->
            Compare.almostEquals(0.0, c2.magnitude) ||
            Compare.almostEquals((c1 / c2).toPolar, (c1.toPolar / c2.toPolar))
        }
    }

    "Cartesian: multiplicative algebraic compatability over R" {
        forAll(Arb.pair(Arb.pair(doubleArb, doubleArb), Arb.pair(cartesianArb, cartesianArb))) { (rs, cs) ->
            val (r1, r2) = rs
            val (c1, c2) = cs
            Compare.almostEquals((r1 * c1) * (r2 * c2), (r1 * r2) * (c1 * c2))
        }
    }

    "Cartesian: R distributes over addition" {
        forAll(Arb.triple(doubleArb, cartesianArb, cartesianArb)) { (r, c1, c2) ->
            Compare.almostEquals(r * (c1 + c2),r * c1 + r * c2)
        }
    }

    "Cartesian: addition over R distributes" {
        forAll(Arb.triple(doubleArb, doubleArb, cartesianArb)) { (r1, r2, c) ->
            Compare.almostEquals((r1 + r2) * c,r1 * c + r2 * c)
        }
    }

    "Polar: multiplicative algebraic compatability over R" {
        forAll(Arb.pair(Arb.pair(doubleArb, doubleArb), Arb.pair(polarArb, polarArb))) { (rs, ps) ->
            val (r1, r2) = rs
            val (p1, p2) = ps
            Compare.almostEquals((r1 * p1) * (r2 * p2), (r1 * r2) * (p1 * p2))
        }
    }

    "Polar: R distributes over addition" {
        forAll(Arb.triple(doubleArb, polarArb, polarArb)) { (r, p1, p2) ->
            Compare.almostEquals(r * (p1 + p2), r * p1 + r * p2)
        }
    }

    "Polar: addition over R distributes" {
        forAll(Arb.triple(doubleArb, doubleArb, polarArb)) { (r1, r2, p) ->
            Compare.almostEquals((r1 + r2) * p,r1 * p + r2 * p)
        }
    }

    "Polar: re and im components same for cartesian conversion" {
        forAll(polarArb) { p ->
            Compare.almostEquals(p.re, p.toCartesian.re)
            Compare.almostEquals(p.im, p.toCartesian.im)
        }
    }

    "Cartesian: re and im components same for polar conversion" {
        forAll(cartesianArb) { c ->
            Compare.almostEquals(c.re, c.toPolar.re)
            Compare.almostEquals(c.im, c.toPolar.im)
        }
    }
})
