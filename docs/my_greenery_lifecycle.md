# My Greenery Local Lifecycle: Register / Edit / Archive

Cross-platform reference for the customer-owned My Greenery lifecycle so future
implementation stays aligned with the product direction and the data boundaries.
iOS (Swift/SwiftUI) and Android (Kotlin/Jetpack Compose) implement this in
parallel. See also
[Catalog → Register → My Greenery Flow](catalog_to_my_greenery_flow.md),
[Architecture & Data Boundaries](architecture_and_data_boundaries.md), and
[Official Read-only Mock Content](mock_official_content_data.md).

## 1. Product intent

- barns is an after-support app in the style of a "My Appliances" registry, but
  for gardening / interior greenery.
- **My Greenery is the primary customer-owned registry** — the greenery the
  customer owns or has installed.
- **Catalog is supporting** official read-only reference content, not the center
  of the app and not a shopping flow.
- **Register / Edit / Archive are local, customer-owned data operations.** They
  never reach a real backend; data stays local-first / mock-first.

## 2. Lifecycle overview

### Register Greenery
- The user creates a local `ProductItem`.
- **Explicit save only** — nothing is persisted until the user saves.

### Register from Catalog
- An official `CatalogItem` is used **only as a safe prefill source**.
- **No auto-save** — the Catalog action only prefills the Register form.
- **No purchase / EC behavior** — registration is local registration, not a buy.

### Edit Greenery (local-only)
- The user edits fields on an existing local `ProductItem`.
- **Explicit save only** — edits are not persisted until the user saves.
- **Official content links remain preserved** (e.g. `careGuideIds`, `categoryId`,
  and `imageUrl` references are not dropped or rewritten by an edit).

### Archive Greenery (local-only)
- **Soft archive only** via `status = archived` (iOS `ProductItemStatus.archived`
  / Android `ProductItemStatus.ARCHIVED`).
- **No hard delete.**
- The **active My Greenery list excludes archived items.**
- The archived item **remains in the local repository**.
- **No restore flow yet** — restore is intentionally out of scope unless future
  work explicitly adds it.

## 3. Data boundaries

| Data | Ownership | Notes |
| --- | --- | --- |
| `ProductItem` | Customer-owned, local-first | Created/edited/archived only via explicit local actions. |
| `CatalogItem` | Official, read-only | Reference only; never mutated; never stored directly as a ProductItem. |
| `GreeneryInfo` / `CareGuide` | Official, read-only | Shown as supporting after-care reference. |
| `RegisterGreeneryPrefill` | Presentation-only bridge | Built from official fields; **not persisted**; carries no customer-owned state. |
| `ConsultationDraft` | Local-only | Preparation note; never submitted. |

Additional rules:

- `careGuideIds` / `categoryId` / `imageUrl` remain **references / metadata**.
  They do **not** imply any Catalog mutation.
- `ProductItem` has **no `catalogItemId` link or migration** — no linkage is
  implied between an owned item and the official Catalog, unless future work
  explicitly adds it.

## 4. Architecture paths

```
Register: View/Screen -> ViewModel -> AddProductItemUseCase    -> ProductItemRepository
Edit:     View/Screen -> ViewModel -> UpdateProductItemUseCase -> ProductItemRepository
Archive:  View/Screen -> ViewModel -> ArchiveProductItemUseCase -> ProductItemRepository.updateProductItem
```

- **Catalog prefill:** Catalog detail → presentation prefill → Register Greenery
  form (presentation layer only; see the Catalog flow doc).
- **Official content:** Item detail → ViewModel → official content use cases →
  repository interfaces.
- Screens render state and trigger actions/navigation only; they do not read JSON
  or a mock repository directly.

Use cases (both platforms):

- iOS — `AddProductItemUseCase`, `UpdateProductItemUseCase`,
  `ArchiveProductItemUseCase`.
- Android — `AddProductItemUseCase`, `UpdateProductItemUseCase`,
  `ArchiveProductItemUseCase`.

Archive is a soft-status update: it routes through
`ProductItemRepository.updateProductItem`, not a delete API.

## 5. Guardrails

- **No auto-save** from Catalog — `ProductItem` is created only on explicit save.
- **No hard delete** in the Archive flow — archive is a soft status change.
- **No purchase / order / payment / estimate / cart / checkout / inventory /
  price** anywhere in this lifecycle.
- No real API, real auth, or sync; mock-first / local-first only.
- No analytics / tracking.
- No production URLs or secrets.
- No customer data is sent anywhere; customer-owned data stays on device.
- No direct JSON / mock-repository access from the presentation layer.

## 6. iOS / Android parity checklist

| Capability | iOS | Android |
| --- | --- | --- |
| Register Greenery | ✅ | ✅ |
| Register from Catalog | ✅ | ✅ |
| Edit Greenery (local-only) | ✅ | ✅ |
| Archive Greenery (soft, local-only) | ✅ | ✅ |
| Active list excludes archived items | ✅ | ✅ |
| No hard delete | ✅ | ✅ |
| Lifecycle boundary tests | ✅ | ✅ |

## 7. Existing test references

These boundaries are covered by deterministic, local-only unit/smoke tests on
both platforms. This doc adds no tests; it references the current ones. Coverage
is intentionally not overstated — these are smoke-level boundary checks.

**iOS** (`apps/ios/Barns/BarnsTests/`)

- `EditGreenerySmokeTests`
  - `testEditFormInitializesFromExistingItem`,
    `testEditingFieldsDoesNotSaveUntilExplicitSave` (explicit save only),
    `testExplicitSaveUpdatesLocalItem`,
    `testUpdateUseCasePreservesOfficialLinksAndImageNil` (official links preserved),
    `testRegisterGreeneryFlowStillAddsNewItem`.
- `ArchiveGreenerySmokeTests`
  - `testArchiveKeepsItemInStoreAndSetsStatus` (soft archive, no delete),
    `testArchivePreservesOfficialLinksAndIdentity`,
    `testActiveListExcludesArchivedItems`,
    `testArchiveOnlyHappensThroughExplicitAction`,
    `testEditFlowStillUpdatesItem`.
- `RegisterFromCatalogSmokeTests` — `testAddItemViewModelAppliesPrefillButDoesNotAutoSave`
  (no auto-save), `testExplicitSavePersistsLocallyWithPrefilledCategory`.

**Android** (`apps/android/app/src/test/java/com/barns/app/`)

- `myitems/EditGreenerySmokeTest`
  - `editFormInitializesFromExistingItem`,
    `editingFieldsDoesNotSaveUntilExplicitSave` (explicit save only),
    `explicitUpdateUpdatesLocalItemAndKeepsCountStable`,
    `registerGreeneryFlowStillAddsNewItem`.
- `myitems/ArchiveGreenerySmokeTest`
  - `archiveKeepsItemInStoreAndSetsStatus` (soft archive, no delete),
    `archivePreservesOfficialLinksAndIdentity`,
    `activeListFilterExcludesArchivedItems`,
    `myItemsViewModelLoadExcludesArchivedItems`,
    `itemDetailViewModelArchivesOnlyOnExplicitAction`.
- `catalog/RegisterFromCatalogSmokeTest` — `addItemViewModelAppliesPrefillButDoesNotAutoSave`
  (no auto-save), `explicitUseCaseSavePersistsLocallyWithPrefilledCategory`.
