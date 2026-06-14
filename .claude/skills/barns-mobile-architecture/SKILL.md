---
name: barns-mobile-architecture
description: Clean Architecture conventions for the barns iOS (Swift/SwiftUI) and Android (Kotlin/Jetpack Compose) apps. Use when adding or structuring app code, layers, data sources, or models.
---

# barns-mobile-architecture

Architecture conventions for the barns mobile apps. Both platforms ship: iOS in Swift/SwiftUI, Android in Kotlin/Jetpack Compose. Both follow Clean Architecture.

## When to use

- When adding or structuring screens, features, or modules in the iOS or Android app.
- When defining data models, repositories, use cases, or data sources.
- When deciding where local-first vs. mock-server data lives.

## Rules

**Layering (Clean Architecture):**
- Separate Presentation, Domain, and Data layers. Dependencies point inward toward Domain.
- Presentation: SwiftUI views + view models (iOS); Composables + view models (Android). No direct data-source access.
- Domain: platform-agnostic entities and use cases; no framework imports.
- Data: repositories implementing domain interfaces, backed by local stores and the mock API.

**Platform conventions:**
- iOS: Swift/SwiftUI, value types where practical, `async/await` for async work.
- Android: Kotlin/Jetpack Compose, coroutines/Flow for async/streams.
- Keep domain logic shareable in spirit (parallel structure across platforms), even though code is per-platform.

**Data flow:**
- Local-first: customer-side data persists locally by default.
- Mock API is read-only for categories, patterns, care guides, notices, company info.
- Never route personal/customer data (items, care logs, drafts, photos, contact info, notes) through the data layer to the mock server.
- Account/login may use mock authentication for MVP.

**Models:**
- Image fields are optional; models and UI must handle `null` images.
- Keep models minimal and MVP-scoped; no fields for out-of-scope features.

## Output expectations

- New code respects layer boundaries (inward dependencies; no cross-layer leaks).
- Local-first and read-only-mock-server boundaries are preserved.
- Models and UI handle null image fields.
- Platform idioms (SwiftUI / Compose) are followed.
