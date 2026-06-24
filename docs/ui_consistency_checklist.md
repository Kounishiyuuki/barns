# UI Consistency Checklist

A lightweight checklist to align iOS and Android UI before any UI consistency
implementation pass begins. Docs-only: this changes no app behavior and approves no
redesign or new features. See also
[Performance & Product Growth Policy](performance_and_product_growth_policy.md),
[MVP Checkpoint](mvp_checkpoint.md), and
[Architecture & Data Boundaries](architecture_and_data_boundaries.md).

## 1. Purpose

- A checklist to run **before** UI consistency implementation passes.
- This is **not** a UI redesign approval.
- This is **not** a request to add new features.
- The app remains **mock-first and local-first**.

## 2. UI direction

- Calm, premium, nature-oriented, trustworthy.
- White / ivory base, deep green, olive, wood / brown, with a subtle warm accent (if applicable).
- After-support feeling — **not** shopping / EC.
- My Greenery should feel **primary**.
- Catalog should remain **supporting** reference content.

## 3. Cross-platform information architecture

Confirm naming and user intent are consistent across iOS and Android for:

- Home
- My Greenery list
- Register Greenery
- Catalog list / detail
- Item detail
- Edit Greenery
- Archive confirmation
- Archived Greenery list
- Restore action
- Consultation draft (local-only areas, if present)
- Settings / privacy areas (if present)

## 4. Visual consistency checklist

- [ ] Spacing / section rhythm
- [ ] Card shape / elevation / border
- [ ] Typography hierarchy
- [ ] Button hierarchy
- [ ] Icon usage
- [ ] Status badges
- [ ] Empty states
- [ ] Error / validation text
- [ ] Confirmation dialogs
- [ ] Destructive / restore actions
- [ ] Image-placeholder behavior (fields may be `null`)
- [ ] List / detail balance

## 5. Interaction consistency checklist

- [ ] Primary actions are clear
- [ ] Secondary actions are not overemphasized
- [ ] Archive is treated as cautious / destructive-like, but **not** a hard delete
- [ ] Restore is clear and safe
- [ ] Save / cancel behavior is explicit
- [ ] Sheets / dialogs dismiss safely
- [ ] Back navigation is predictable
- [ ] No hidden auto-save unless already intended
- [ ] No real submission / order / payment behavior implied

## 6. Copy and terminology checklist

- [ ] "My Greenery" terminology is consistent
- [ ] Register Greenery / Edit Greenery / Archive / Restore names are consistent
- [ ] Catalog is described as official reference / supporting content
- [ ] Local-only consultation drafts are **not** described as submitted
- [ ] Avoid shopping / EC wording except as guardrails
- [ ] Avoid implying real auth / API / sync
- [ ] Japanese / English copy reviewed consistently where applicable

## 7. iOS UI consistency checklist

- [ ] SwiftUI layout consistency
- [ ] Navigation titles
- [ ] Toolbar actions
- [ ] Sheets
- [ ] Alerts / confirmation dialogs
- [ ] Form sections
- [ ] List rows / cards
- [ ] Empty states
- [ ] Dynamic Type / basic accessibility considerations
- [ ] Portrait layout
- [ ] Avoid unnecessary heavy animations or decoration

## 8. Android UI consistency checklist

- [ ] Compose layout consistency
- [ ] Top app bars / titles
- [ ] Navigation actions
- [ ] Dialogs
- [ ] Forms
- [ ] List rows / cards
- [ ] Empty states
- [ ] Content descriptions / basic accessibility
- [ ] Emulator size responsiveness
- [ ] Avoid unnecessary heavy animations or decoration

## 9. Performance-conscious UI rules

- Keep screens simple.
- Avoid expensive startup UI work.
- Avoid unnecessary global state.
- Avoid heavy image / media assumptions.
- Avoid large visual rewrites in one PR.
- Prefer small, reusable UI patterns only when they reduce complexity.
- No added dependencies unless explicitly justified.

## 10. Data boundary UI rules

- `ProductItem` is customer-owned, local-first data.
- `CatalogItem` / `GreeneryInfo` / `CareGuide` are read-only reference content.
- `RegisterGreeneryPrefill` is only a UI bridge, not persisted coupling.
- `ConsultationDraft` remains local-only and is never described as submitted.
- Archive / Restore are status changes (`active` ↔ `archived`), not hard delete.
- The UI must not imply customer data is sent anywhere.

## 11. Recommended implementation sequence (planning only)

Do **not** start these here:

1. iOS UI consistency pass
2. Android UI consistency pass
3. Settings / legal / privacy polish docs
4. iOS Settings polish
5. Android Settings polish
6. Care task / log polish
7. Consultation draft local review polish
