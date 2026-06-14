---
name: barns-project-guard
description: Guards the barns MVP scope and data/privacy boundaries. Use before adding features, endpoints, or data flows to barns. Blocks out-of-scope work (admin, orders, payments, chat, scheduling) and prevents sending personal customer data to the mock server.
---

# barns-project-guard

barns is an after-sales support app for existing customers of a wall-greenery / interior-green company. Its purpose is post-purchase / post-construction customer satisfaction — not new-customer acquisition. This skill keeps changes inside MVP scope and inside the data/privacy boundaries.

## When to use

- Before adding any feature, screen, endpoint, or data flow.
- When deciding whether something belongs in the MVP.
- When wiring anything to the mock API server.
- When handling customer-entered data (items, care logs, notes, photos, contact info).

## Rules

**Scope — out of scope for MVP (do not add):**
- Staff admin / management consoles
- Order management, estimates, payment
- Chat / messaging
- Scheduling / booking
- In-app order submission

**Scope — in scope:**
- Existing-customer after-sales support features.
- Account/login (MVP may use mock authentication).
- External inquiries handled as **phone consultation guidance** only.

**Data & privacy (local-first):**
- Most customer-side data is local-first.
- Never send to the mock server: registered items, care logs, consultation drafts, photos, addresses, phone numbers, personal notes.
- The mock server may serve only: categories, patterns, care guides, notices, company info.
- A company-owned server may be integrated later — do not assume it now.

**Content safety:**
- No real customer data, real addresses, real phone numbers, or unauthorized images.
- No secrets, API keys, `.env`, or `.dev.vars` committed.

**Images:**
- Images are optional for the first MVP. UI and data models must work when image fields are `null`.

## Output expectations

- Proposed changes stay within MVP scope; out-of-scope requests are flagged, not implemented.
- No personal/customer data crosses to the mock server.
- Mock-server interactions touch only the allowed read-only content types.
- Image-null states are handled.
