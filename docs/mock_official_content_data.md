# Official Read-only Mock Content

Concise reference for the official, read-only mock data that backs barns'
supporting content: catalog candidates, greenery basic information, and care
guides. This is the foundation that iOS/Android repositories and screens will
consume later. See also
[Architecture & Data Boundaries](architecture_and_data_boundaries.md).

## Purpose

- Provide official, **read-only** reference content as mock data **before** any
  UI or repository is built.
- Keep this content **mock now, real company data/API later** behind the same
  shapes, so it can be swapped without UI changes.
- This content is the **same for every user** and holds **no personal state**.

## Files

| File | Holds | Source today | Future |
| --- | --- | --- | --- |
| `shared/mock-data/catalog-items.json` | Official product / installation / greenery candidates | Read-only mock | Read-only API |
| `shared/mock-data/greenery-info.json` | Official basic information per greenery | Read-only mock | Read-only API |
| `shared/mock-data/care-guides.json` | Official growing / care instructions | Read-only mock | Read-only API |

All `imageUrl` fields are `null`; images are optional and may stay `null`.

## ProductItem vs. CatalogItem

| | `ProductItem` (My Greenery) | `CatalogItem` (Catalog) |
| --- | --- | --- |
| Ownership | **Customer-owned**, local-first | **Official**, read-only |
| Mutability | User edits (location, notes, status) | Same for all users; not user-edited |
| Examples of fields | `locationLabel`, `notes`, `status`, `careGuideIds` | `greeneryInfoId`, `careGuideIds`, `kind` |
| Personal state | Yes (private) | **Never** |

They stay separate so customer-private state never leaks into shared official
content, and official content is never frozen behind per-user state. A catalog
candidate can later be registered by the user as their own `ProductItem`.

## GreeneryInfo vs. CareGuide

- **GreeneryInfo** — *what a greenery is*: overview, difficulty, recommended
  environment, light preference, watering overview, maintenance notes. Concise,
  UI-friendly summary for a basic-information screen.
- **CareGuide** — *how to care for it*: structured steps for watering,
  sunlight/placement, cleaning, seasonal care, plus frequency and cautions.
  Structured enough for iOS/Android to render as a step list later.

A `CatalogItem` may link to one `GreeneryInfo` (`greeneryInfoId`, nullable) and
several `CareGuide`s (`careGuideIds`).

## How mock maps to future real API

- Field shapes stay the same; only the **source** changes (mock JSON →
  read-only API response).
- IDs (`catalog-*`, `greenery-info-*`, `guide-*`) remain the stable join keys.
- Only **official read-only** repositories may later point at a real API.
  Customer-owned data stays local.

## How iOS/Android should consume this later

```
Screen -> ViewModel -> UseCase -> Repository Interface -> Mock Repository (now)
                                                       -> API Repository (later)
```

- Screens never read mock JSON or call an API client directly.
- ViewModels depend on use cases; use cases depend on repository interfaces.
- Swapping mock → API happens at the repository layer only.

## Must NOT be stored in these official files

- User-owned state: `locationLabel`, personal `notes`, `status` of ownership
- Care logs / care tasks tied to a user
- Consultation drafts (local-only, never here)
- Real company data, real customer data, real addresses, real phone numbers
- Production URLs, secrets, credentials, real image URLs
- Analytics / tracking markers

## UI guidance

- Keep screens **simple, concise, readable**.
- **My Greenery first** — it is the primary experience.
- Catalog is **supporting official content**, not the center of the app.
- **No cart / order / payment / sales-heavy flow.** Catalog reads as official
  reference / additional candidates.
- Cards should show an overview and the next useful action.
