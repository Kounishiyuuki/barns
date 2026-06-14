# Issue 001: Mock API server (read-only support data)

> GitHub-issue-ready planning document. Implementation is **not** part of this issue — this defines scope and contract for the follow-up build task.

## Title

feat(server): mock API server for read-only support data

## Background

barns is an after-sales support app for existing customers of a wall-greenery / interior-green company. Customer-side data is **local-first**. Before wiring the iOS (Swift/SwiftUI) and Android (Kotlin/Jetpack Compose) clients to remote data, the MVP needs a lightweight **mock API server** that serves static/support content only.

The mock server reads from the existing `shared/mock-data/*.json` files and exposes them over HTTP. It must be designed so a future company-owned server can replace it behind the same repository/data-source interface (see `docs/04_data_api_design.md`).

## Scope

- A lightweight local HTTP server that serves **read-only support data** plus a **mock auth** response and a **current mock user**.
- Reads existing `shared/mock-data/*.json` as its data source.
- Serves only: categories, patterns, care guides, notices, company info.
- Provides a mock login response and a current-user endpoint (no real auth).
- Health endpoint for readiness checks.
- CORS allowed for local development only.

## Out of scope

- Any client/app code changes (iOS/Android).
- Persisting, receiving, or storing customer-side private data.
- Real authentication, authorization, audit logging, or data isolation (these are required only for the future production/company server).
- Staff admin, order management, estimates, payment, chat, scheduling, in-app order submission.
- Endpoints for registered items, care logs, care tasks, consultation drafts (these stay **local-first**; see Risks/Notes).

## Implementation tasks

- [ ] Choose a lightweight server runtime consistent with the repo (do not add heavy frameworks without justification — flag for approval if a new dependency/package manager is needed).
- [ ] Load and serve `shared/mock-data/*.json` (categories, patterns, care-guides, notices, company-info).
- [ ] Implement `GET /health`.
- [ ] Implement `POST /auth/login` returning a static mock token + user.
- [ ] Implement `GET /me` returning the current mock user.
- [ ] Implement the read-only data endpoints (see API endpoints).
- [ ] Implement `GET /patterns/:id` (single lookup) with 404 on miss.
- [ ] Enable CORS for local development only.
- [ ] Return consistent JSON envelopes and error shapes.
- [ ] Add a short server README (run instructions, allowed data boundary).
- [ ] Add validation (JSON validity, endpoint smoke checks).

## API endpoints

| Method | Path | Purpose | Source |
|---|---|---|---|
| GET | `/health` | Readiness/liveness check | — |
| POST | `/auth/login` | Mock login; returns mock token + user | static |
| GET | `/me` | Current mock user | static |
| GET | `/categories` | Item categories | `categories.json` |
| GET | `/patterns` | Wall-greenery patterns | `patterns.json` |
| GET | `/patterns/:id` | Single pattern (404 if absent) | `patterns.json` |
| GET | `/care-guides` | Care guides | `care-guides.json` |
| GET | `/notices` | Notices / announcements | `notices.json` |
| GET | `/company-info` | Company info | `company-info.json` |

All non-health, non-auth endpoints are **read-only (GET)**. No write/update/delete endpoints exist for customer data.

## Response format

Consistent JSON, image fields nullable (`imageUrl: string | null`).

**Success (collection):**
```json
{
  "data": [ /* items */ ]
}
```

**Success (single):**
```json
{
  "data": { /* item */ }
}
```

**Health:**
```json
{ "status": "ok" }
```

**Mock login (`POST /auth/login`):**
```json
{
  "data": {
    "userId": "mock-user-001",
    "displayName": "Demo User",
    "token": "mock-token"
  }
}
```
Request body is accepted but not persisted; no real credentials are validated or stored.

**Current user (`GET /me`):**
```json
{
  "data": { "userId": "mock-user-001", "displayName": "Demo User" }
}
```

**Error:**
```json
{ "error": { "code": "not_found", "message": "Pattern not found" } }
```

## Security/privacy notes

- The mock server is **read-only for support data** + mock auth/user. It must **never receive or store** customer-side private data: registered items, care logs, consultation drafts, photos, addresses, phone numbers, personal notes.
- Mock login does not validate or persist credentials; the token is a fixed mock string, not a real secret.
- No secrets, API keys, `.env`, or `.dev.vars` are introduced. Mock token is non-sensitive and clearly fake.
- CORS is enabled for local development only; production hardening (auth, authz, audit logging, data isolation) is explicitly deferred to the future company server.
- All served data must contain only obviously fake placeholders — no real customer data, real addresses, or real phone numbers.
- External inquiries remain **phone consultation guidance**; the server provides no inquiry/submission endpoint.
- Data minimization: serve only the fields the clients need.

## Validation/test plan

- **JSON validity:** validate each served `shared/mock-data/*.json` with `jq` (or equivalent).
- **Endpoint smoke tests:** `GET /health` returns `{ "status": "ok" }`; each GET endpoint returns the expected envelope and status 200; `GET /patterns/:id` returns 200 for a known id and 404 for an unknown id; `POST /auth/login` returns the mock user/token.
- **Boundary check:** confirm there are no endpoints that accept/store private data (no POST/PUT/DELETE for items, care logs, drafts, photos, contacts, notes).
- **Privacy scan:** grep served data for real-looking PII; confirm placeholders only.
- **Run instructions** verified against the server README.

## Acceptance criteria

- [ ] All listed endpoints exist and return the documented response shapes.
- [ ] `GET /health` returns `{ "status": "ok" }`.
- [ ] `POST /auth/login` and `GET /me` return the mock user without validating/storing credentials.
- [ ] Read endpoints serve data from `shared/mock-data/*.json`.
- [ ] `imageUrl` (and other image fields) tolerate `null`.
- [ ] No endpoint receives or stores customer-side private data.
- [ ] No secrets/keys/env files added; mock token is clearly fake.
- [ ] CORS limited to local development.
- [ ] Validation/test plan passes; server README documents how to run.

## Risks/notes

- **Contract discrepancy with `docs/04_data_api_design.md`:** that doc currently lists `/product-items`, `/care-tasks`, `/care-logs`, and `/consultation-drafts` endpoints, and uses `POST /auth/mock-login`. This issue intentionally **excludes** the private-data endpoints (they must remain local-first) and uses `POST /auth/login` per the current task. **Open question:** should `docs/04` be updated to mark those endpoints as local-only and align the auth path? Recommend a small follow-up docs change once this is agreed.
- **Runtime/dependency choice** is open. Adding a new dependency or package manager should be confirmed before implementation (approval-gate).
- **Future migration:** keep responses behind a stable repository/data-source interface so the company server can replace the mock without client changes; introduce API versioning at that point.
