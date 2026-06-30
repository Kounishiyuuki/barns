# Mock Image Asset Plan

## 1. Purpose

This plan defines a local mock image asset pack for barns MVP manual QA and stakeholder demos.
The assets are intended to support a calm, premium, local-first/mock-first after-support experience without implying real customer data, production support, commerce, or backend behavior.

The local mock image pack has been generated for this PR. The files are mock-only, local-only demo assets and are not wired into either app UI or platform mock seed data.

## 2. Current Repository Findings

- Current app demo seed content lives in platform mock repositories, not shared mock-data JSON.
- iOS mock repositories live under `apps/ios/Barns/BarnsApp/Data/Repositories/`.
- Android mock repositories live under `apps/android/app/src/main/java/com/barns/app/data/repository/`.
- `shared/mock-data/` contains mock-api-oriented JSON content and currently keeps image fields as `null`.
- Existing docs already state that image references are optional and the app should work with missing images.
- No existing committed local mock image asset directory was found.

## 3. Existing Image Field/Rendering Support

- iOS domain entities already include optional `imageUrl` fields for ProductItem, CatalogItem, GreeneryInfo, CareGuide, CareTask, and CareLog related models.
- Android domain models already include optional `imageUrl` fields for the corresponding ProductItem, CatalogItem, GreeneryInfo, CareGuide, CareTask, and CareLog models.
- Current platform mock repositories mostly seed those image fields as `nil` or `null`.
- No active iOS or Android local asset rendering path was identified in the presentation layer during this survey.
- This PR does not wire images into app UI, change model schemas, or change mock repository seed behavior.

## 4. Recommended Asset Directory Structure

Recommended final local mock asset structure:

```text
shared/mock-assets/images/
  catalog/
  my-greenery/
  care/
  hero/
  manifest.json
```

Actual structure in this PR:

```text
shared/mock-assets/images/
  catalog/
  my-greenery/
  care/
  hero/
  manifest.json
```

The category directories contain the generated local mock image files described by the manifest.

## 5. Generated Image Categories and Counts

Planned image count: 32.

| Category | Count | Purpose |
| --- | ---: | --- |
| Catalog / installed greenery reference | 10 | Official read-only reference imagery for Catalog list/detail demos |
| My Greenery owned-item images | 14 | Customer-owned local registry demo imagery |
| Care / guide support images | 6 | Local after-care and care guide support imagery |
| Hero / demo background images | 2 | Premium demo background and overview imagery |

Generated image count in this PR: 32.

## 6. Filename Convention

- Lowercase kebab-case.
- Stable semantic prefix by category: `catalog-`, `my-greenery-`, `care-`, or `hero-`.
- One-based two-digit concept suffix is not required unless multiple variants are generated later.
- Preferred file extension after optimization: `.jpg` for high-quality JPEGs with smaller size and no visible quality loss.

Example:

```text
catalog-living-room-moss-wall-01.jpg
```

## 7. Manifest Schema

The manifest uses a top-level metadata object and an `items` array. Each item contains:

- `id`
- `filename`
- `relativePath`
- `category`
- `intendedUse`
- `altText`
- `mockOnly`
- `generatedWith`
- `generationStatus`
- `width`
- `height`
- `fileSizeBytes`
- `prompt`
- `notes`

For this PR, every item is marked `generationStatus: "generated"` and records the local generation route, square dimensions, and file size in bytes. Raw generation responses and remote URLs are intentionally not stored.

## 8. Full Manifest Location

Full generated asset manifest:

```text
shared/mock-assets/images/manifest.json
```

The manifest is the source of truth for future generation prompts, intended use, alt text, and category mapping.

## 9. Prompt Catalog

The exact prompt for every planned image is stored in `shared/mock-assets/images/manifest.json`.
Each prompt explicitly requires:

- no text
- no logos
- no people
- no real company signage
- no contact information
- no sales/order/payment cues
- realistic premium interior photography
- brand-neutral mock/demo asset

Prompt style direction:

- realistic, high-quality, premium interior photography
- calm modern Japanese/Scandinavian-inspired interior
- warm daylight
- ivory, wood, stone, deep green, and olive tones
- square composition suitable for mobile cards and detail hero images
- no UI screenshots, watermarks, prices, carts, checkout, reservation, estimate, inquiry, or sales cues

## 10. Alt Text Catalog

The alt text catalog is stored per item in the manifest. Alt text should describe the visible mock scene without implying real customers, real locations, production inventory, or service submission.

Examples:

- "Framed moss wall installation in a bright living room"
- "Local registered pothos plant on a warm wood shelf"
- "Watering can beside a healthy indoor plant"

## 11. Proposed Mapping to Current Platform Mock Seed Items

This PR does not change platform mock seed data. The following is a future mapping proposal only:

- ProductItem / My Greenery seeds can map to `my-greenery-*` assets after a local asset rendering path is approved.
- CatalogItem / GreeneryInfo / CareGuide seeds can map to `catalog-*` and `care-*` assets while remaining official read-only reference content.
- CareTask and CareLog demo states can use `care-*` assets only as local after-care visual support.
- Hero assets can support Home or demo-only backgrounds if a future UI PR explicitly approves that display.
- `RegisterGreeneryPrefill` should remain a UI bridge only and should not become persisted coupling through image references.

Any future mapping should preserve existing explicit save behavior and should not introduce auto-save, network loading, sync, upload, or storage permissions.

## 12. Integration Phases After This PR

1. Perform visual review for no text, logos, people, watermarks, contact data, prices, carts, order/payment cues, or real signage before any future UI wiring.
2. Add local asset references to platform mocks only if current model fields can support them without schema or behavior changes.
3. Add iOS and Android presentation support in separate small PRs if local asset rendering is approved.
4. Run platform-specific tests/builds only when app code, mock data behavior, or rendering is changed.

## 13. Validation Checklist

- `git diff --check` is clean.
- Changed files are limited to this plan, the generated local mock image files, and the generated manifest.
- No app source, server/mock-api, shared/mock-data, model, ViewModel, UseCase, Repository, build, dependency, signing, CI, generated, IDE, or local files are changed.
- No image files are faked or represented by placeholders.
- No remote image URLs are introduced.
- No secrets, credentials, API keys, tokens, raw API responses, logs, production URLs, real phone numbers, real addresses, real emails, real company data, real customer data, analytics, or tracking are introduced.
- Sales/EC/order/payment/estimate terms appear only as guardrails or out-of-scope language.

## 14. Guardrails / Not in Scope

This PR does not:

- implement app image rendering
- change platform mock repository seed values
- change shared/mock-data JSON
- add network image loading
- add remote URLs
- add image upload, camera, photo library, file picker, or storage permissions
- change schemas, domain models, repositories, use cases, view models, or persistence behavior
- change server/mock-api endpoint behavior
- add real API/auth/sync/cloud persistence
- add backend submission, chat, ticketing, scheduling, order, estimate, payment, analytics, tracking, or hard delete
- add production company details, real contact details, real customer data, or real images

## 15. Risks and Follow-up Decisions

- Generated images need visual QA before they are wired into either app.
- The team should decide whether local app asset integration should happen via platform resource bundles, shared copied assets, or a build-time sync step.
- Future app display work should remain presentation-only unless a separate PR explicitly approves data/model behavior changes.
- Asset size budget should be reviewed before app-bundle integration.
