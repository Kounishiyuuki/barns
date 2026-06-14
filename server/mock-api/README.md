# barns mock API

Lightweight, **read-only** mock API server for the barns MVP. It serves static
support data from `shared/mock-data/*.json` plus a mock auth response, so the
iOS/Android clients can confirm MVP flows before a real company server exists.

Built with Node's built-in `http` module — **no external dependencies**.

## Run

```bash
cd server/mock-api
npm start          # http://localhost:8787  (override with PORT=xxxx)
```

## Test

```bash
cd server/mock-api
npm test
```

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Liveness check |
| POST | `/auth/login` | Mock login (body ignored; returns mock user + token) |
| GET | `/me` | Current mock user |
| GET | `/categories` | Categories |
| GET | `/patterns` | Patterns |
| GET | `/patterns/:id` | Single pattern (404 if unknown) |
| GET | `/care-guides` | Care guides |
| GET | `/notices` | Notices |
| GET | `/company-info` | Company info |

## Response envelope

```json
{ "data": { /* ... */ }, "error": null }
```

```json
{ "data": null, "error": { "code": "not_found", "message": "..." } }
```

## Privacy & security boundary

This server is **read-only support data only**. It must **never** receive,
store, or expose customer-side private data: registered items, care logs,
consultation drafts, photos, addresses, phone numbers, or personal notes.

- `POST /auth/login` ignores the request body; it is never stored or logged.
- Request logging is limited to `METHOD path` — no bodies, headers, or query strings.
- The mock token is a fixed, non-secret placeholder, not a real credential.
- No secrets, API keys, `.env`, or `.dev.vars` are committed. See `.dev.vars.example`.
- CORS is enabled for local development only.
