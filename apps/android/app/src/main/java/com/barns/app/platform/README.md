# platform

Platform holds Android-specific services used by the data/presentation layers
through abstractions. This is the Android counterpart of the iOS
Infrastructure layer.

Planned services:
- secure storage (auth token; no plaintext)
- local notifications (care reminders)
- phone opening (phone consultation guidance)
- image storage (optional; images may be null)
- config (non-secret build/runtime settings only)

Rules:
- The domain layer must not depend on anything here.
- No secrets, API keys, production URLs, analytics, or tracking.
