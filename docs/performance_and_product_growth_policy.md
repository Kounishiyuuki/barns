# Performance & Product Growth Policy

A lightweight policy to keep barns fast, clear, stable, and high quality — avoiding
unnecessary feature bloat — while preserving the plan to implement features the
product owner explicitly requests, in safe phases. Docs-only: this changes no app
behavior. See also
[MVP Checkpoint](mvp_checkpoint.md),
[Architecture & Data Boundaries](architecture_and_data_boundaries.md), and
[Release Readiness Checklist](release_readiness_checklist.md).

## 1. Purpose

- Keep barns lightweight, fast, understandable, and high quality.
- Avoid unnecessary feature bloat.
- Preserve future implementation of features the product owner explicitly requests.
- The app remains **mock-first and local-first** until real API/auth/sync is explicitly introduced.

## 2. Product principle

- barns is an **after-support app, not an everything app**.
- **My Greenery** stays the center of the experience.
- **Catalog supports** My Greenery — it is read-only reference, not shopping / EC.
- Quality, trust, and clarity matter more than the number of features.

## 3. Performance principle

- Fast launch.
- Smooth navigation.
- Simple screen state.
- No unnecessary network dependency.
- No unnecessary heavy image / media pipeline.
- No broad global state.
- No expensive startup initialization.
- Keep lists and cards simple.
- Avoid unnecessary animations or decoration that reduce usability.
- Prefer small, focused data models.

## 4. Feature growth policy

- Do **not** add features just because they are possible.
- If the product owner **explicitly** wants a feature, treat it as a valid future
  implementation candidate — not bloat.
- Implement requested features in safe phases:
  1. Docs / requirements clarification
  2. Local-only or mock-first foundation
  3. iOS implementation
  4. Android parity
  5. QA / docs update
  6. Real API / auth / sync **only when explicitly approved**
- Avoid combining multiple feature families in one PR.

## 5. Feature classification

**Core now**
- My Greenery lifecycle
- Catalog reference support
- Care guide / basic info display
- Local consultation draft support
- Settings / privacy clarity

**Good next candidates**
- UI consistency
- Care task / log polish
- Consultation draft review polish
- Settings / legal / privacy polish
- Internal build preparation

**Later, with explicit approval**
- Notifications
- Image upload
- Real auth
- Real API / sync
- Company contact submission
- Scheduling
- Chat

**Separate phase / product expansion**
- Order
- Estimate
- Payment
- Staff / admin console
- CRM / customer database integration

## 6. PR sizing policy

- Small, reviewable PRs.
- Prefer one platform **or** one documentation topic per PR.
- Keep iOS / Android parity through paired PRs.
- Avoid broad rewrites.
- Avoid unrelated cleanup.
- Do not mix performance / UI / data / API / auth changes in one PR unless explicitly justified.

## 7. UI / UX policy

- Simple, calm, premium, nature-oriented UI.
- My Greenery should be visually primary.
- Catalog should not overpower the owned registry.
- Clear primary / secondary actions.
- Empty states should be useful but not noisy.
- Avoid visual clutter.
- Prefer readability and trust over flashy UI.

## 8. Data and architecture policy

- `ProductItem` remains local-first, customer-owned data.
- `CatalogItem` / `GreeneryInfo` / `CareGuide` remain official read-only content.
- `ConsultationDraft` remains local-only until explicit submission is approved.
- `RegisterGreeneryPrefill` should not become persisted coupling.
- Archive / Restore remain soft status updates (`active` ↔ `archived`).
- No hard delete unless explicitly approved.
- Preserve the flow: Presentation (View / Screen) → ViewModel → UseCase → Repository interface.

## 9. Guardrails

- No real API / auth / sync unless explicitly approved
- No production endpoint / secrets
- No analytics / tracking
- No image upload
- No chat / scheduling / order / payment / estimate by default
- No staff / admin by default
- No generated / build files
- No broad architecture rewrites
- No hard delete

## 10. How to evaluate future feature requests

Before starting a requested feature, confirm:

- [ ] Does this support the after-support experience?
- [ ] Does this strengthen My Greenery?
- [ ] Can it be local-first / mock-first first?
- [ ] Can it be implemented without slowing launch / navigation?
- [ ] Can it be split into iOS and Android parity PRs?
- [ ] Does it preserve data boundaries?
- [ ] Does it avoid implying real submission / payment / order behavior?
- [ ] Is the added complexity worth the user value?

## 11. Recommended next PRs (planning only)

Do **not** start these here:

1. UI consistency checklist docs
2. iOS UI consistency pass
3. Android UI consistency pass
4. Settings / legal / privacy polish docs
5. Care task / log polish
6. Consultation draft local review polish
7. Internal build preparation docs
