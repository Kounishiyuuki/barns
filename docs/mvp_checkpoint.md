# MVP Checkpoint — My Greenery Lifecycle Parity

A planning checkpoint that records the current MVP state after Register / Edit /
Archive / Restore reached iOS ↔ Android parity, so the next phase can be planned
without blurring product scope or data boundaries. Docs-only: this changes no app
behavior. See also
[My Greenery Lifecycle](my_greenery_lifecycle.md),
[Catalog → Register → My Greenery Flow](catalog_to_my_greenery_flow.md), and
[Architecture & Data Boundaries](architecture_and_data_boundaries.md).

## 1. Checkpoint summary

- The full **My Greenery local lifecycle — Register, Register from Catalog, Edit,
  Archive, Archived list, Restore — is now cross-platform (iOS + Android) and
  local-only.**
- This is a meaningful milestone: the customer-owned registry now has a complete
  create → maintain → soft-archive → restore loop on both platforms, with **no
  hard delete** and **no customer data leaving the device**.
- The lifecycle stays **mock-first / local-first**; no real API, auth, or sync has
  been introduced. This checkpoint marks a safe point to plan the next phase.
- A follow-up **cross-platform UI completion pass** has since aligned local-only /
  read-only / not-submitted wording and small UI polish across Home, My Greenery,
  the lifecycle, Catalog list / detail, Care, Consultation Draft, Phone guidance,
  Support, and Settings. See the
  [Device QA Checklist](device_qa_checklist.md#9-wording--boundary-parity-after-the-cross-platform-ui-completion-pass)
  for the wording/boundary parity checks. This is UI/wording polish only — it does
  **not** add backend, auth, sync, submission, or release readiness.

## 1.1 MVP demo checkpoint update (after PR #77–#82)

Since the lifecycle-parity milestone above, a further phase of local mock image
work, UI polish, and security/privacy planning has landed. The MVP is now
suitable for a **local demo / stakeholder walkthrough**, while remaining
**mock-first / local-first** and **not production-ready**.

### Current MVP capabilities (both platforms, local-only)

- Local **My Greenery** registry with the full local Register / Edit / Archive /
  Archived list / Restore lifecycle (soft status only, no hard delete).
- Local **Care Tasks / Care Logs** as on-device after-care records (no reminders,
  notifications, or sync).
- Official **read-only Catalog / GreeneryInfo / CareGuide** reference content.
- **Support** guidance and local **Consultation Draft** preparation (never
  submitted).
- **Settings / Legal / Privacy / About** informational placeholder/readiness
  sections communicating local-first / mock-first MVP status (not final legal or
  release approval).
- **Local mock image assets** (generated, optimized, brand-neutral demo images)
  rendered in **Catalog and My Greenery** only, via a `mock://<category>/<asset>`
  reference resolved by an **exact allowlist** (category + asset name). No network
  image loading; `nil` / unknown / `http(s)` references stay safe. These are
  mock/demo images only — **not** real customer or company photos.

### Recent UI / image / security / QA improvements

- Generated, optimized local mock images and their Catalog / My Greenery
  rendering (see [Mock Image Asset Plan](mock_image_asset_plan.md)).
- Improved Home hierarchy (calm dashboard hero, consistent entry rows).
- Android Home scroll resilience and calmer list empty states; visible list
  reassurance footers.
- iOS Settings readability (full-width detail rows).
- iOS detail hero consistency (rounded, inset card) matching Android.
- Care recent-log readability (care kind + date instead of a bare date).
- A **partial, screenshot-based visual QA** round (iOS Simulator + Android
  emulator) documented in [MVP Visual QA Pass](mvp_visual_qa_pass.md) and
  [MVP UI Excellence Pass](mvp_ui_excellence_pass.md). This is **not** full
  real-device QA across a device matrix.
- A **security / privacy hardening roadmap** exists as planning only
  ([Security & Privacy Hardening Plan](security_privacy_hardening_plan.md)); it is
  **not yet implemented** as production hardening.
- **Internal-release readiness scaffolding**: a minimal, accurate iOS
  [PrivacyInfo.xcprivacy](ios_privacy_manifest_notes.md) and an
  [Android Data Safety draft](android_data_safety_draft.md), plus Settings
  App Status / Data & Privacy / Release Readiness sections. These are readiness
  scaffolding only — **not** production security, final privacy/legal approval,
  or store readiness.

### Current readiness level

- **Suitable for**: local MVP demo / stakeholder walkthrough.
- **Not**: production-ready, App Store / TestFlight-ready, or internal-Android-
  release-ready — those depend on the release tasks in
  [Release Readiness Checklist](release_readiness_checklist.md) being completed.
- **Manual real-device QA is still required** (see
  [Device QA Checklist](device_qa_checklist.md)); only partial simulator /
  emulator screenshot QA has been performed so far.

## 2. Current completed capabilities

### iOS (Swift / SwiftUI)
- My Greenery list / detail
- Register Greenery
- Register from Catalog (prefill)
- Edit Greenery (local-only)
- Archive Greenery (soft, local-only)
- Archived Greenery list
- Restore archived greenery
- Official basic info / care guide sections (read-only)
- Local Care Tasks / Care Logs (on-device records)
- Support guidance / local Consultation Draft (never submitted)
- Settings / Legal / Privacy / About informational sections
- Local mock image rendering in Catalog / My Greenery (allowlisted, no network)

### Android (Kotlin / Jetpack Compose)
- My Greenery list / detail
- Register Greenery
- Register from Catalog (prefill)
- Edit Greenery (local-only)
- Archive Greenery (soft, local-only)
- Archived Greenery list
- Restore archived greenery
- Official basic info / care guide sections (read-only)
- Local Care Tasks / Care Logs (on-device records)
- Support guidance / local Consultation Draft (never submitted)
- Settings / Legal / Privacy / About informational sections
- Local mock image rendering in Catalog / My Greenery (allowlisted, no network)

### Documentation
- [Architecture & Data Boundaries](architecture_and_data_boundaries.md)
- [Catalog → Register → My Greenery Flow](catalog_to_my_greenery_flow.md)
- [My Greenery Lifecycle](my_greenery_lifecycle.md)
- [Official Read-only Mock Content](mock_official_content_data.md)
- [Mock Image Asset Plan](mock_image_asset_plan.md)
- [MVP Visual QA Pass](mvp_visual_qa_pass.md) / [MVP UI Excellence Pass](mvp_ui_excellence_pass.md)
- [Security & Privacy Hardening Plan](security_privacy_hardening_plan.md)
- [Release Readiness Checklist](release_readiness_checklist.md) / [Device QA Checklist](device_qa_checklist.md)

## 3. Product scope confirmation

barns remains an **after-support app** in the style of a "My Appliances" registry,
for gardening / interior greenery:

- **Customer-owned registry first** — My Greenery is the primary surface.
- **Catalog is supporting** official read-only reference content, not the center
  of the app.
- **Not** a shopping / EC experience.
- **Not** a staff / admin console.
- **Not** order / estimate / payment / scheduling / chat.

These remain out of scope and are listed here only as guardrails.

## 4. Data boundaries

| Data | Ownership | Notes |
| --- | --- | --- |
| `ProductItem` | Customer-owned, local-first | Created / edited / archived / restored only via explicit local actions. |
| `CatalogItem` | Official, read-only | Reference only; never mutated; never stored directly as a ProductItem. |
| `GreeneryInfo` / `CareGuide` | Official, read-only | Supporting after-care reference. |
| `RegisterGreeneryPrefill` | Presentation-only bridge | Built from official fields; not persisted; carries no customer-owned state. |
| `ConsultationDraft` | Local-only | Preparation note; never submitted. |

- **Archive and Restore are soft status updates only** (`active` ↔ `archived`),
  routed through `ProductItemRepository.updateProductItem`.
- **No hard delete** and no re-create / re-insert; stored item count stays stable.
- **No `catalogItemId` link or migration** between an owned item and the Catalog.
- **No customer data is sent anywhere**; customer-owned data stays on device.

## 5. Architecture checkpoint

- **MVVM + Clean Architecture + Repository Pattern** is preserved across both
  platforms.
- Flow stays **Presentation → ViewModel → UseCase → Repository interface**.
- Mock repositories remain isolated behind repository interfaces.
- **No direct JSON / mock-repository access from Presentation.**
- **Domain has no platform / network / storage dependencies** (no SwiftUI/UIKit on
  iOS, no Android/Compose on Android in the domain layer).

## 6. Test / validation posture

Coverage is **smoke-level local boundary** testing, not exhaustive UI coverage.
The tests are deterministic and local-only.

**iOS** (`apps/ios/Barns/BarnsTests/`)
- `RegisterFromCatalogSmokeTests` — prefill applied, no auto-save, explicit save.
- `EditGreenerySmokeTests` — explicit save only, official links preserved.
- `ArchiveGreenerySmokeTests` — soft archive, no delete, active list excludes archived.
- `RestoreGreenerySmokeTests` — archived-list filtering, explicit restore, stable
  count, preserved fields, active-list reload, official content separation.
- `ItemOfficialContentSmokeTests` / `OfficialContentSmokeTests` / `CatalogPresentationSmokeTests`
  — official read-only content boundaries.

**Android** (`apps/android/app/src/test/java/com/barns/app/`)
- `catalog/RegisterFromCatalogSmokeTest` — prefill applied, no auto-save, explicit save.
- `myitems/EditGreenerySmokeTest` — explicit save only, official links preserved.
- `myitems/ArchiveGreenerySmokeTest` — soft archive, no delete, active list excludes archived.
- `myitems/RestoreGreenerySmokeTest` — archived-list filtering, explicit restore, stable
  count, preserved fields, active/archived reload, official content non-mutation.
- `myitems/ItemOfficialContentSmokeTest` / `catalog/OfficialContentSmokeTest` /
  `catalog/CatalogPresentationSmokeTest` — official read-only content boundaries.

**CI expectation**
- iOS CI (`.github/workflows/ios-ci.yml`): build for the iOS Simulator and run the
  `BarnsTests` smoke tests.
- Android CI (`.github/workflows/android-ci.yml`): `./gradlew testDebugUnitTest`
  and `assembleDebug` on JDK 17.

## 7. Remaining MVP-safe next steps (planning only)

Not implemented here — listed for planning:

- Care task / log polish
- Consultation draft review polish (still local-only, never submitted)
- Settings / legal / privacy polish
- Offline / mock data QA
- Release readiness docs
- UI consistency pass (iOS ↔ Android)
- Optional archived / restore UX polish

## 8. Explicitly not yet

Out of scope at this checkpoint (no work started):

- Real auth
- Real API / sync
- Cloud persistence
- Image upload
- User photo handling (local mock images are demo assets only, not user photos)
- Push / local notifications (unless explicitly planned)
- Chat
- Scheduling
- Order / estimate / payment
- Analytics / tracking
- Support ticket / in-app submission (external inquiries stay phone guidance only)
- Staff / admin console
- Hard delete
- Production URLs / secrets
- Real company / customer / contact data

Guardrail summary: local-only / mock-only, no remote image loading, no secrets,
no production URL, no real support submission, a clear separation between mock
demo images and any future user photos, and a security/privacy roadmap that
exists but is **not yet implemented** as production hardening.

## 9. Recommended next PRs

A short ordered recommendation (do **not** start these here):

1. `docs/release-readiness-checklist` — capture what "shippable MVP" means.
2. iOS / Android UI consistency pass — align copy and layout across platforms.
3. Consultation draft local review polish — keep it local-only, never submitted.
4. Care task / log QA polish — verify the existing local care surfaces.
