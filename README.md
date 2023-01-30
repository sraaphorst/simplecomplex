# SimpleComplex

A very simple implementation of complex numbers in Kotlin.

- Numbers derive from `ComplexBase` and there are two concrete subclasses,
  namely:
  - `Polar`
  - `Cartesian`

- Testing is done using property-based testing with `kotest`.
- All code is included in a single file, namely `Complex.kt` which can
  just be dropped into a project.
- Developed to solve equations with higher dimension than quadratics for
  my implementation of [The Ray Tracer Challenge](http://raytracerchallenge.com/).
  - https://github.com/sraaphorst/raytracer-kotlin

*Status:* _In progress._