# Settings / Legal / Privacy Polish Plan

A lightweight plan for the Settings / Legal / Privacy polish, written before
implementation so future iOS / Android Settings PRs have clear scope and do not
imply real API / auth / sync, real submission, or production release readiness.
Docs-only: this changes no app behavior. See also
[UI Consistency Checklist](ui_consistency_checklist.md),
[Architecture & Data Boundaries](architecture_and_data_boundaries.md), and
[Release Readiness Checklist](release_readiness_checklist.md).

## 1. Purpose

- Plan the Settings / Legal / Privacy polish **before** implementation.
- This is **not** a legal approval.
- This is **not** a privacy policy finalization.
- This is **not** store-readiness approval.
- The app remains **mock-first and local-first**.

## 2. Settings polish goals

- Make the app's current status understandable.
- Clarify the local-only / mock-first behavior.
- Clarify what the app can and cannot do today.
- Keep Settings simple and not overloaded.
- Support user trust without implying a production backend or real submission.

## 3. Recommended Settings information architecture (planning only)

- App information
- About barns
- Local data / privacy explanation
- Support / contact guidance (if already present or planned)
- Legal / notices
- Version / build information (if available later)

> Developer / debug-only items should not appear in user-facing release UI unless
> explicitly intended.

## 4. Local-first privacy messaging

- My Greenery (`ProductItem`) is local-first, customer-owned data.
- `ConsultationDraft` is local-only and not submitted.
- `CatalogItem` / `GreeneryInfo` / `CareGuide` are official read-only reference content.
- `RegisterGreeneryPrefill` is a UI bridge only.
- Archive / Restore are soft status updates (`active` ↔ `archived`).
- No hard delete.
- No customer data is sent anywhere in the current MVP.
- No real API / auth / sync yet.

## 5. Legal / notice messaging

- Use placeholder / legal-planning language only.
- Do not add real company legal claims unless provided and approved.
- Avoid final privacy policy claims unless explicitly approved.
- Avoid terms-of-service finalization unless explicitly approved.
- Avoid real company contact details unless provided and approved.
- If wording becomes user-facing later, make clear it is an MVP / internal planning state.

## 6. Support / contact boundary

- barns may guide users to prepare consultation information.
- Current local consultation drafts are **not** submitted.
- No chat.
- No scheduling.
- No order / estimate / payment.
- No real support-ticket submission.
- No customer data transmission.
- Phone / contact guidance must not imply in-app submission unless explicitly implemented later.

## 7. Platform-specific future implementation notes

- iOS Settings polish should keep the SwiftUI structure simple.
- Android Settings polish should keep the Compose structure simple.
- Keep iOS / Android terminology aligned.
- Keep Settings copy consistent with the My Greenery / Catalog / ConsultationDraft boundaries.
- Avoid adding dependencies.
- Avoid changing bundle / package / signing / build settings.

## 8. Security / privacy guardrails

- No secrets.
- No production endpoint.
- No analytics / tracking.
- No real customer / company data.
- No real phone / address / image data.
- No image upload.
- No external storage.
- No push notifications.
- No cloud sync.
- No login / auth.
- No payment / order / estimate data.

## 9. QA checklist for future Settings polish

- [ ] Settings loads without crash.
- [ ] Copy is consistent across iOS / Android.
- [ ] Local-only explanations are visible and understandable.
- [ ] No real submission is implied.
- [ ] No production URLs / secrets.
- [ ] No analytics / tracking.
- [ ] No legal / privacy final claims unless approved.
- [ ] No app behavior or data boundary changes unless intentionally scoped.
- [ ] Accessibility / readability check.

## 10. Recommended implementation sequence (planning only)

Do **not** start these here:

1. iOS Settings / Legal / Privacy polish
2. Android Settings / Legal / Privacy polish
3. Settings copy parity review
4. Device QA update (if needed)
5. Release readiness checklist update (if needed)
