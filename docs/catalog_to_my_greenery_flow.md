# Catalog → Register → My Greenery Flow (QA Reference)

Narrow QA/reference for the end-to-end MVP experience that connects the
supporting Catalog to the primary My Greenery registry, and the data/architecture
boundaries that keep it safe. iOS (Swift/SwiftUI) and Android (Kotlin/Jetpack
Compose) implement this in parallel. See also
[Architecture & Data Boundaries](architecture_and_data_boundaries.md) and
[Official Read-only Mock Content](mock_official_content_data.md).

## 1. Product intent

- **My Greenery is primary** — the customer's registered owned/installed greenery.
- **Catalog is supporting** official read-only reference content, not the center
  of the app and not a shopping flow.
- **Register from Catalog is local registration, not purchase.** It only prefills
  the existing Register Greenery form; the user still saves locally.

## 2. User flow

1. Home → Catalog (a low-emphasis supporting entry; My Greenery stays primary).
2. Catalog list → Catalog detail.
3. Catalog detail → "Register to My Greenery" (local registration action).
4. The Register Greenery form opens **prefilled** (name, category, a compatible
   initial type) from the official `CatalogItem`.
5. The user **explicitly saves** — nothing is saved on tapping the Catalog action.
6. My Greenery then contains the local `ProductItem`.
7. Item detail shows official **Basic information** and **Care guide** sections.

## 3. Data boundary

| Data | Ownership | Notes |
| --- | --- | --- |
| `CatalogItem` | Official, read-only | Reference only; never mutated; never stored directly as a ProductItem. |
| `GreeneryInfo` / `CareGuide` | Official, read-only | Shown as supporting after-care reference. |
| `ProductItem` | Customer-owned, local-first | Created only on explicit save. |
| `ConsultationDraft` | Local-only | Preparation note; never submitted. |
| `RegisterGreeneryPrefill` | Presentation-only bridge | Built from official fields; **not persisted**; carries no customer-owned state. |

Key rule: official content flows **into** a prefill, but the prefill is a
one-way, presentation-only copy. `ProductItem` has no `catalogItemId`, so no
linkage or migration is implied by this flow.

## 4. Architecture path

```
Catalog list/detail screen -> ViewModel -> Catalog/GreeneryInfo/CareGuide UseCases
                                        -> Repository interfaces -> Mock repositories
Register screen            -> AddItemViewModel -> AddProductItemUseCase
                                        -> ProductItemRepository (local)
Item detail                -> ItemDetailViewModel -> official content use cases
```

- Screens render state and trigger actions/navigation only.
- No screen reads JSON or a mock repository directly; everything goes through
  use cases that depend on repository interfaces.
- The prefill bridges Catalog → Register at the presentation layer only.

## 5. Safety / guardrails

- **No auto-save** from Catalog — `ProductItem` is created only on explicit save.
- **No purchase / order / payment / estimate / cart / checkout / inventory /
  price** anywhere in the Catalog or Register flow.
- No real API, real auth, or sync; mock-first / local-first only.
- No analytics / tracking.
- No production URLs or secrets.
- No customer data is sent anywhere; customer-owned data stays on device.
- Images are optional; official `imageUrl` fields remain `null` in the MVP.

## 6. iOS / Android parity checklist

| Capability | iOS | Android |
| --- | --- | --- |
| Home supporting Catalog entry | ✅ | ✅ |
| Catalog list | ✅ | ✅ |
| Catalog detail | ✅ | ✅ |
| Register from Catalog prefill | ✅ | ✅ |
| Explicit save only (no auto-save) | ✅ | ✅ |
| Item detail official info / care guide | ✅ | ✅ |
| Boundary tests | ✅ | ✅ |

## 7. Existing test coverage

These boundaries are already covered by deterministic, local-only unit/smoke
tests on both platforms (no new tests added by this QA pass).

**iOS** (`apps/ios/Barns/BarnsTests/`)

- `CatalogPresentationSmokeTests`
  - `testCatalogPresentationHasNoShoppingOrOwnedFields` — no shopping/EC or
    customer-owned fields in catalog presentation.
  - `testCatalogListLoadsThroughUseCase`, `testCatalogDetailResolvesLinkedOfficialContent`,
    `testCatalogDetailWithoutGreeneryInfoDegradesSafely`, `testUnknownCatalogItemIsNotFound`.
- `RegisterFromCatalogSmokeTests`
  - `testAddItemViewModelAppliesPrefillButDoesNotAutoSave` — no auto-save.
  - `testExplicitSavePersistsLocallyWithPrefilledCategory` — explicit save creates a local ProductItem.
  - `testPrefillHasNoCustomerOwnedFields`, `testPrefillCopiesOfficialFieldsOnly`,
    `testPrefillTypeForNonWallGreenIsOwned`, `testCatalogDetailContentExposesPrefill`.
- `ItemOfficialContentSmokeTests`
  - `testOfficialContentIsSeparateFromProductItemState` — ProductItem and official content stay separate.

**Android** (`apps/android/app/src/test/java/com/barns/app/`)

- `catalog/CatalogPresentationSmokeTest`
  - `catalogPresentationHasNoShoppingOrOwnedFields`.
  - `catalogListPresentationBuildsThroughUseCase`, `catalogDetailResolvesLinkedOfficialContent`,
    `detailWithoutGreeneryInfoDegradesSafely`, `unknownCatalogItemIsNull`.
- `catalog/RegisterFromCatalogSmokeTest`
  - `addItemViewModelAppliesPrefillButDoesNotAutoSave` — no auto-save.
  - `explicitUseCaseSavePersistsLocallyWithPrefilledCategory` — explicit save creates a local ProductItem.
  - `prefillHasNoCustomerOwnedFields`, `prefillCopiesOfficialFieldsOnly`, `prefillTypeForNonWallGreenIsOwned`.
- `myitems/ItemOfficialContentSmokeTest`
  - `officialContentHasNoCustomerOwnedFields` — official content carries no customer-owned fields.

CatalogItem-is-not-mutated and not-stored-directly is enforced structurally:
`RegisterGreeneryPrefill` copies only official fields, and both platforms verify
its fields contain no customer-owned state.
