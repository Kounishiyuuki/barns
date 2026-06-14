---
name: barns-docs-writer
description: Writing standards for barns documentation (README, docs/, design notes). Use when creating or editing barns docs to keep them concise, accurate, MVP-scoped, and free of personal or sensitive data.
---

# barns-docs-writer

Standards for documentation in the barns repository so docs stay accurate, concise, and aligned with MVP scope and privacy rules.

## When to use

- When creating or editing files under `docs/`, `design/`, or `README.md`.
- When documenting features, data models, or APIs for barns.
- When adding examples, sample data, or screenshots to docs.

## Rules

- Keep docs concise and specific; prefer short sections, lists, and tables over long prose.
- Describe only what exists or is in MVP scope. Mark future/optional items clearly (e.g. "later", "optional").
- Reflect the real architecture: iOS (Swift/SwiftUI), Android (Kotlin/Jetpack Compose), Clean Architecture, local-first data, mock API first.
- Document the mock server as read-only for: categories, patterns, care guides, notices, company info.
- Never include real customer data, real addresses, real phone numbers, secrets, or unauthorized images. Use clearly fake placeholders.
- Note that external inquiries are handled as phone consultation guidance.
- State that images are optional and fields may be `null`.
- Do not document out-of-scope features (admin, orders, estimates, payment, chat, scheduling, in-app order submission) as if they exist.
- Use English for commit-facing docs unless the file is explicitly localized.

## Output expectations

- Docs are short, scannable, and accurate to current scope.
- All sample data is fake and safe; no secrets or PII.
- Future/optional features are clearly labeled, not implied as shipped.
- Privacy and local-first boundaries are reflected where data is described.
