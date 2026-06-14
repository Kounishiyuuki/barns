# Infrastructure

Infrastructure contains platform-specific services used by Data and Presentation.

Planned areas:

- Auth: mock authentication boundary and future secure token storage.
- Persistence: local-first storage implementation.
- Network: HTTP client implementation when mock API integration is approved.
- Notification: OS local care reminders.
- Phone: user-initiated phone consultation opening.
- ImageStorage: future local image handling.
- Config: non-secret build/runtime settings only.

Do not commit secrets, API keys, production URLs, analytics, tracking, `.env`, or secret `.xcconfig` files.
