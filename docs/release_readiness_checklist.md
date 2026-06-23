# Release Readiness Checklist

A lightweight checklist of what must be verified — and what must stay out of scope —
before any TestFlight / internal Android distribution / store-readiness work begins.
Docs-only: this changes no app behavior. See also
[MVP Checkpoint](mvp_checkpoint.md),
[Architecture & Data Boundaries](architecture_and_data_boundaries.md), and
[My Greenery Lifecycle](my_greenery_lifecycle.md).

## 1. Purpose

- This is **not a release approval** and does not authorize any build to ship.
- It is a pre-flight checklist before TestFlight / internal Android distribution /
  store-readiness work is started.
- The app remains **mock-first and local-first**; no real API, auth, or sync is
  introduced by this checklist.

## 2. Current release-candidate scope

What the current app actually contains (iOS + Android, local-only):

- My Greenery lifecycle — Register, Register from Catalog (prefill), Edit, Archive,
  Archived list, Restore. The active list excludes archived items; no hard delete.
- Catalog reference support (official, read-only).
- Official basic info / care guide sections (read-only).
- Local consultation draft (`ConsultationDraft`) — preparation note only, never submitted.
- Settings screen (both platforms).
- Security / privacy and legal/release docs:
  [05_security_privacy.md](05_security_privacy.md),
  [08_legal_release_operation.md](08_legal_release_operation.md).
- **No** real account / sync / API.

## 3. Must remain out of release scope (for now)

Guardrails — none of these are in the release candidate and must not be added here:

- Real auth
- Production API / sync
- Cloud persistence
- Image upload
- Chat
- Scheduling
- Order / estimate / payment
- Analytics / tracking
- Staff / admin console
- Hard delete

## 4. iOS readiness checklist

- [ ] `xcodebuild` build for the iOS Simulator passes
- [ ] `BarnsTests` smoke tests pass
- [ ] Bundle identifier / display name reviewed *(only if already configured; do not change here)*
- [ ] Signing / distribution status — **to be checked later**, not part of this checklist
- [ ] `PrivacyInfo.xcprivacy` / privacy manifest status — **not present yet**; add when release work starts
- [ ] No production URLs / secrets
- [ ] No real customer / company data
- [ ] No tracking / analytics
- [ ] Local-only behavior verified (no customer data leaves the device)
- [ ] Screenshots / store copy — **not required yet** unless release work starts

## 5. Android readiness checklist

- [ ] `./gradlew assembleDebug` passes
- [ ] `./gradlew testDebugUnitTest` (unit tests) pass
- [ ] Package / application id reviewed *(only if already configured; do not change here)*
- [ ] Signing / release build status — **to be checked later**, not part of this checklist
- [ ] No production URLs / secrets
- [ ] No real customer / company data
- [ ] No tracking / analytics
- [ ] Local-only behavior verified (no customer data leaves the device)
- [ ] Store listing — **not required yet** unless release work starts

## 6. Privacy / security checklist

- [ ] No secrets / API keys / `.env` / `.dev.vars` committed
- [ ] No production endpoint configured
- [ ] No user / customer data transmission to any server
- [ ] No analytics / tracking SDKs
- [ ] No external storage / image upload
- [ ] No contact / phone / address collection beyond local, customer-owned fields (if any)
- [ ] No payment / order data
- [ ] No push notifications (unless explicitly planned and added later)

External inquiries remain **phone consultation guidance only**; no in-app submission.

## 7. Data boundary checklist

| Data | Expected boundary |
| --- | --- |
| `ProductItem` | Customer-owned, local-first |
| `CatalogItem` / `GreeneryInfo` / `CareGuide` | Official, read-only; never mutated |
| `ConsultationDraft` | Local-only; never submitted |
| `RegisterGreeneryPrefill` | Presentation-only; not persisted |

- [ ] Archive / Restore are **soft status updates** (`active` ↔ `archived`) only
- [ ] **No hard delete** and no re-create / re-insert; stored item count stays stable
- [ ] No `catalogItemId` link / migration between owned items and the Catalog

## 8. CI / test checklist

- [ ] iOS CI (`.github/workflows/ios-ci.yml`): Simulator build + `BarnsTests` smoke tests
- [ ] Android CI (`.github/workflows/android-ci.yml`): `assembleDebug` + `testDebugUnitTest` on JDK 17
- [ ] Smoke / local-boundary tests referenced in [MVP Checkpoint](mvp_checkpoint.md#6-test--validation-posture) still pass
- [ ] Docs / link checks (internal doc links resolve)
- [ ] Secret / privacy grep clean
- [ ] No generated / build output committed
- [ ] `git diff --check` clean

## 9. Known release blockers / not ready yet

- No confirmed Apple / Google distribution setup (none assumed present)
- No production backend / auth / sync
- No release screenshots / store metadata
- No final company legal approval
- No final privacy policy / store data-safety declarations
- No device QA matrix
- No accessibility / localization QA pass

## 10. Recommended next PRs (planning only)

Do **not** start these here:

1. iOS / Android UI consistency pass
2. Settings / legal / privacy polish
3. Consultation draft local review polish (keep local-only, never submitted)
4. Care task / log QA polish
5. TestFlight / internal build preparation docs
6. Device QA checklist
