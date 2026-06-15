# di

Dependency injection wiring.

For the source skeleton, a minimal manual `DependencyContainer` lives in the
`app` package (mirroring iOS). This package is reserved for a DI framework
(e.g. Hilt) or expanded manual wiring once features are implemented.

Rules:
- DI wiring must not leak DTOs or data-source types into presentation.
- No new dependencies are added in the skeleton.
