# Device QA Checklist

A lightweight manual QA checklist for validating the current MVP behavior on
simulators / devices before TestFlight / internal Android distribution preparation.
Docs-only: this changes no app behavior. See also
[Release Readiness Checklist](release_readiness_checklist.md),
[MVP Checkpoint](mvp_checkpoint.md), and
[My Greenery Lifecycle](my_greenery_lifecycle.md).

## 1. Purpose

- Manual QA checklist to run before TestFlight / internal Android build preparation.
- This is **not a release approval** and does not authorize any build to ship.
- The app remains **mock-first and local-first**; no real API, auth, or sync is exercised.

## 2. Test environments

- [ ] iOS Simulator
- [ ] iOS physical device — *later*
- [ ] Android emulator
- [ ] Android physical device — *later*

> The exact device matrix is **still pending**; this list is a starting point, not the
> final matrix.

## 3. Cross-platform core flow checklist

Run on both iOS and Android:

- [ ] App launches without crash
- [ ] Enter as guest / mock user (if applicable)
- [ ] Home loads
- [ ] My Greenery list loads
- [ ] Register Greenery (manual entry) succeeds after explicit save
- [ ] Register from Catalog prefill — fields prefilled, saved only on explicit action
- [ ] Item detail displays
- [ ] Official basic info / care guide sections display (read-only)
- [ ] Edit Greenery — changes persist after explicit save
- [ ] Archive Greenery — item moves out of the active list
- [ ] Archived list shows the archived item
- [ ] Restore archived greenery
- [ ] Active list excludes archived items
- [ ] Restored item returns to the active list
- [ ] No hard-delete behavior anywhere (count stays stable)

## 4. iOS-specific QA checklist

- [ ] Navigation (push/pop) behaves correctly
- [ ] Sheet / dialog presentation and dismissal behave correctly
- [ ] Back / dismiss returns to the expected screen
- [ ] Form validation and field readability
- [ ] Empty states render correctly (empty My Greenery, empty archived list)
- [ ] Dynamic Type / basic accessibility (if applicable)
- [ ] Portrait layout
- [ ] No unexpected network / auth prompts
- [ ] No crash during Register / Edit / Archive / Restore

## 5. Android-specific QA checklist

- [ ] Navigation behaves correctly
- [ ] Dialog behavior is correct
- [ ] System back behavior is correct
- [ ] Form validation and field readability
- [ ] Empty states render correctly (empty My Greenery, empty archived list)
- [ ] Basic accessibility labels / content descriptions where applicable
- [ ] Layout holds on common emulator sizes
- [ ] No unexpected network / auth prompts
- [ ] No crash during Register / Edit / Archive / Restore

## 6. Data boundary QA

- [ ] `CatalogItem` remains read-only (never mutated)
- [ ] Register from Catalog creates only a local `ProductItem`, and only after explicit save
- [ ] Edit changes only customer-owned `ProductItem` fields
- [ ] Archive / Restore change only status (`active` ↔ `archived`)
- [ ] `ConsultationDraft` remains local-only and is never submitted
- [ ] No customer data is sent anywhere (local-only verified)
- [ ] No production URLs / secrets present

## 7. Negative / guardrail checks

Confirm none of these appear or are reachable:

- [ ] No purchase / order / payment / estimate / cart / checkout
- [ ] No scheduling / chat
- [ ] No real API / auth / sync
- [ ] No analytics / tracking
- [ ] No image upload
- [ ] No hard delete
- [ ] No staff / admin flow

## 8. Regression checklist

- [ ] Register still works after a restore flow
- [ ] Edit still works after archive / restore
- [ ] Archive still works after edit
- [ ] Restore does not duplicate items
- [ ] Active / archived list counts behave as expected
- [ ] Official info / care guide links remain visible after edit / archive / restore where appropriate

## 9. Known gaps

- No confirmed real distribution setup (none assumed present)
- No final device matrix
- No accessibility QA pass yet
- No localization QA pass yet
- No store screenshot / metadata QA
- No final legal / privacy approval

## 10. Recommended next PRs (planning only)

Do **not** start these here:

1. iOS / Android UI consistency pass
2. Settings / legal / privacy polish
3. Consultation draft local review polish (keep local-only, never submitted)
4. Care task / log QA polish
5. Internal build preparation docs
