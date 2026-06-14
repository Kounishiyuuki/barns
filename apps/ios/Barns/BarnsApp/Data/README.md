# Data

Data contains repository implementations, DTOs, mappers, and local/remote data sources.

Initial skeleton policy:

- DTOs stay inside Data and must not be passed to Presentation.
- Repository implementations conform to Domain repository protocols.
- Local data sources own customer-side private data such as registered items, care logs, consultation drafts, photos, addresses, phone numbers, and personal notes.
- Remote data sources are limited to approved support data and mock authentication until a future company server design is approved.
- No mock API integration is implemented in this skeleton.
