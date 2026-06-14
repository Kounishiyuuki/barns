# UseCases

UseCases coordinate one user-facing action at a time and depend only on Domain entities and repository protocols.

Initial skeleton policy:

- Keep business rules here, not in SwiftUI views or repository implementations.
- ViewModels must call UseCases instead of DataSources or API clients directly.
- Customer-side private data remains local-first unless a future approved server-sync design changes this boundary.
