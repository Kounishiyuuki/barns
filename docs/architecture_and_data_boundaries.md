# Architecture & Data Boundaries

Concise reference for how barns is structured, where customer-owned data ends
and official read-only content begins, and how mock data is replaced with real
company data later. Keep this aligned with the apps as they evolve.

Status note: this document describes the current MVP plus clearly-labeled
future direction. Items marked **(planned)** do not exist yet.

## 1. Product concept

- barns is **not** a generic plant app or a shopping app.
- It is an **after-support app** centered on the customer's registered
  owned/installed greenery.
- The product role is a **customer-owned item registry + support** experience,
  similar in role to appliance support apps (not a copy of any private app).
- **My Greenery** (registered owned/installed greenery) is the primary
  experience.
- The catalog is **supporting official read-only content**, not the center of
  the app.

## 2. Architecture overview

- **Modular Clean Architecture** — layered, dependencies point inward.
- **MVVM** — SwiftUI/Compose screens driven by ViewModels.
- **Repository Pattern** — domain depends on repository interfaces, not sources.
- **Mock-first / Local-first** — customer data stays on device; official
  content is mock now, real API later.
- **Feature-based separation** — code is grouped by feature, not by layer only.

## 3. Architecture rationale

Why this structure fits barns:

- **Mock-to-real replacement** — official content can move from mock to a real
  API by swapping a repository implementation, with no screen changes.
- **iOS/Android parity** — both apps follow the same layers and feature names,
  keeping behavior consistent across platforms.
- **Testability** — domain and ViewModels are tested with in-memory fakes; no
  network or database needed for unit/smoke tests.
- **Change resistance** — UI, domain, and data evolve independently behind
  stable interfaces.
- **Clear data separation** — customer-owned data and official read-only data
  live behind different repositories and never blur together.

Architectural influence (safe wording):

- Android's recommended app architecture separates a UI layer from a data
  layer, and can add a domain layer for reusable business logic.
- SwiftUI apps benefit from separating model data from UI state.
- Clean Architecture keeps domain/use cases independent from UI, database,
  network, and platform details.

These are general, public guidelines. We do **not** claim that Panasonic or any
other private app uses this exact architecture.

## 4. Layer responsibilities

| Layer | Holds | Notes |
| --- | --- | --- |
| **Presentation** | SwiftUI/Compose screens, ViewModels, display models / presentation helpers | No direct data-source access; ViewModels call use cases only. |
| **Domain** | Entities, UseCases, repository interfaces, platform-independent rules | No SwiftUI/UIKit/Compose/Android/Room/Retrofit/DataStore/network imports. |
| **Data** | Local repositories (customer data), mock/read-only repositories (official content), future API repositories | Implements domain interfaces; DTOs never leak into Presentation. |
| **Infrastructure** | Future platform-specific services only when needed **(planned)** | Added only when a concrete need appears. |

## 5. Data ownership boundaries

### Customer-owned, local-first

Stays on the device; never sent to the mock server.

- `ProductItem` / My Greenery
- location label
- memo / notes
- care logs and tasks (while local)
- `ConsultationDraft`

### Official, read-only

Served by the mock server now, by a company API later. No personal data.

- `CatalogItem` **(planned)**
- greenery info / basic information
- `CareGuide` / growing instructions
- patterns (`WallGreenPattern`)
- notices (`Notice`)
- `CompanyInfo`

### Support-preparation data

- `ConsultationDraft` remains **local-only** in the MVP. It is preparation for
  contacting support and is **not submitted** anywhere.

### Why `ProductItem` and `CatalogItem` stay separate

- `ProductItem` is **private customer state** — what this user owns/installed,
  where it is, and their notes. It is local-first and editable by the user.
- `CatalogItem` is **official shared content** — the same for every user and
  read-only.
- Mixing them would either leak customer data into shared/official content or
  freeze official content behind per-user state. Separate models keep
  ownership, mutability, and sync rules clear and let each side change
  independently (local store vs. future API).

## 6. Repository boundaries

Current repositories (exist on both iOS and Android):

| Repository | Ownership | Source today | Future |
| --- | --- | --- | --- |
| `ProductItemRepository` | Customer-owned My Greenery | Local-first (in-memory mock) | Local store |
| `ConsultationDraftRepository` | Local-only draft, **no submission** | Local-first (in-memory mock) | Local store |
| `CareRepository` | Care guides / tasks / logs | Mock | Read-only API for guides; local for customer logs |
| `PatternRepository` | Wall-greening patterns | Read-only mock | Read-only API |
| `SupportRepository` | Company info / support guidance | Read-only mock | Read-only API |
| `HomeRepository` | Home summary composition | Mock | Composed from above |
| `AuthRepository` | Mock authentication | Mock | Real auth (later) |

Planned repositories (not yet present):

- `CatalogRepository` **(planned)** — official read-only catalog; mock now,
  API later.
- Dedicated `CareGuideRepository` / `NoticeRepository` / `CompanyInfoRepository`
  **(planned)** — split out of the current repositories if the read-only
  surface grows; mock now, API later.

Rule: customer-owned repositories stay local; official repositories are the
only ones that may later point at a real API.

## 7. Feature boundaries

| Feature | Responsibility |
| --- | --- |
| **MyGreenery** | Registered owned/installed greenery: list, detail, register flow. |
| **Catalog** **(planned)** | Official products / additional candidates (supporting discovery). |
| **CareGuide / GreeneryInfo** | Basic information and growing instructions (read-only). |
| **Support** | Consultation draft and phone consultation guidance only. |
| **Settings** | App state and guardrails. |

## 8. Mock-to-real-data migration strategy

1. Start with **mock repositories behind repository interfaces** (current
   state).
2. Replace **read-only** mock repositories with **API repositories** later —
   same interface, new implementation, no screen changes.
3. Keep **customer-owned local data separate**; it does not move to a server by
   default.
4. **Do not send customer data** anywhere until a flow is explicitly designed
   and approved.
5. **Screens never depend on mock JSON or API clients directly** — only on use
   cases, which depend on repository interfaces.

## 9. UI/UX guidelines

- Keep screens **simple, readable, and concise**.
- **My Greenery first** — it is the primary experience.
- Catalog is **supporting discovery**, not a main shopping flow.
- Cards should show an **overview and the next useful action**.
- Use calm, support-oriented wording; **avoid sales-heavy language**.
- Avoid overloading one screen with long explanations.

## 10. MVP guardrails / out of scope

Not in the MVP:

- No real authentication yet
- No server sync yet
- No image upload yet
- No QR / barcode scanning yet
- No chat
- No scheduling / booking
- No order / estimate / payment
- No analytics / tracking
- No production URLs or secrets

External inquiries are handled as **phone consultation guidance** only.
Images are optional; models and UI must work when image fields are `null`.
