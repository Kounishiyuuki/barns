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
- Local Care Tasks / Care Logs (on-device after-care records; no reminders / sync).
- Catalog reference support (official, read-only).
- Official basic info / care guide sections (read-only).
- Local consultation draft (`ConsultationDraft`) — preparation note only, never submitted.
- Support phone guidance (guidance only; no in-app submission).
- Settings / Legal / Privacy / About informational sections (both platforms).
- Local mock image rendering in Catalog / My Greenery — generated, optimized,
  brand-neutral demo assets resolved by an **exact `mock://` allowlist**
  (category + asset name), bundled locally with **no network image loading**;
  `nil` / unknown / `http(s)` references stay safe. Not real customer/company photos.
- UI/demo-readiness polish landed across Home, My Greenery, Catalog, Care, Support,
  and Settings (see [MVP Visual QA Pass](mvp_visual_qa_pass.md) and
  [MVP UI Excellence Pass](mvp_ui_excellence_pass.md)).
- Security / privacy and legal/release docs:
  [05_security_privacy.md](05_security_privacy.md),
  [08_legal_release_operation.md](08_legal_release_operation.md), and the
  [Security & Privacy Hardening Plan](security_privacy_hardening_plan.md)
  (planning roadmap only — **not** implemented as production hardening).
- **No** real account / sync / API.

### Readiness level

- **Suitable for**: a local MVP demo / stakeholder walkthrough.
- **Not**: production-ready, App Store / TestFlight-ready, or internal-Android-
  release-ready. Those require the tasks below to be completed and approved.
- Only **partial simulator / emulator screenshot QA** has been performed; full
  manual real-device QA is still required.

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
- [ ] App icon / launch screen reviewed *(only if already configured; do not change here)*
- [ ] Signing / provisioning / distribution status — **to be checked later**, not part of this checklist
- [ ] `PrivacyInfo.xcprivacy` / privacy manifest status — **not present yet**; review/add when release work starts
- [ ] No production URLs / secrets
- [ ] No real customer / company data
- [ ] No tracking / analytics
- [ ] Local-only behavior verified (no customer data leaves the device)
- [ ] Accessibility / Dynamic Type / manual VoiceOver check on a real device — **still required**
- [ ] Performance smoke check on a real device — **still required**
- [ ] Screenshots / store copy — **not required yet** unless release work starts

## 5. Android readiness checklist

- [ ] `./gradlew assembleDebug` passes
- [ ] `./gradlew testDebugUnitTest` (unit tests) pass
- [ ] Package / application id reviewed *(only if already configured; do not change here)*
- [ ] App icon / launch / display name reviewed *(only if already configured; do not change here)*
- [ ] Signing / release build status (internal release build readiness) — **to be checked later**, not part of this checklist
- [ ] No production URLs / secrets
- [ ] No real customer / company data
- [ ] No tracking / analytics
- [ ] Local-only behavior verified (no customer data leaves the device)
- [ ] Accessibility / large font scale / manual TalkBack check on a real device — **still required**
- [ ] Performance smoke check on a real device — **still required**
- [ ] Data Safety form preparation — **not prepared yet**; prepare when release work starts
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
- iOS `PrivacyInfo.xcprivacy` privacy manifest not added yet
- Android Data Safety form not prepared yet
- No device QA matrix; only partial simulator / emulator screenshot QA performed so far
- No accessibility / Dynamic Type / manual VoiceOver / TalkBack pass on real devices
- No performance smoke check on real devices
- No localization QA pass
- Security / privacy hardening roadmap not yet implemented as production hardening

## 10. Recommended next PRs (planning only)

Do **not** start these here:

1. iOS / Android UI consistency pass
2. Settings / legal / privacy polish
3. Consultation draft local review polish (keep local-only, never submitted)
4. Care task / log QA polish
5. TestFlight / internal build preparation docs
6. Device QA checklist
