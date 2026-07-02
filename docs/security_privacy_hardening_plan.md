# Security & Privacy Hardening Plan

A forward-looking roadmap for how barns should protect user data **before** any
real API, authentication, cloud sync, photo upload, or support submission is
added. This is **docs-only planning**: it implements nothing and changes no app
behavior. See also [05. セキュリティ・プライバシー](05_security_privacy.md)
(baseline policy) and [Architecture & Data Boundaries](architecture_and_data_boundaries.md).

> Status separation: sections marked **Current** describe what exists in the
> MVP today. Sections marked **Future** describe hardening that is planned but
> **not implemented** and must not be assumed present. Nothing here implies
> production security or release readiness.

## 1. Purpose

- Define how personal information should be protected as barns grows from a
  local-first / mock-first MVP toward optional real backend features.
- Keep the current MVP intentionally simple while committing, in writing, to a
  strong privacy posture later.
- Give each future feature (real auth, real API, cloud sync, image upload,
  support submission, notifications, analytics) a clear security bar to clear
  **before** it ships.
- Prevent scope creep: features that collect or transmit personal data require
  an explicit decision, not an incremental default.

## 2. Current MVP security posture (Current)

barns is **local-first and mock-first**. As of this plan:

- No real authentication, no real API, no cloud sync, no cloud storage.
- No image upload, no camera / photo-library access, no file picker.
- No analytics, tracking, crash-reporting SDKs, or ad SDKs.
- No production URLs, secrets, tokens, or real contact data in the app or repo.
- Customer-side data (My Greenery, Care Tasks / Logs, Consultation Drafts) is
  **local-only** and never leaves the device.
- Catalog / GreeneryInfo / CareGuide is **official read-only reference** content
  served from mock sources; it carries no customer-owned state.
- Images are **local mock/demo assets** only, bundled with the app and rendered
  from an allowlisted `mock://` reference. No network image loading.
- External inquiries are **phone consultation guidance only** — nothing is
  submitted from the app.

This posture is low-risk precisely because little sensitive data exists and
nothing is transmitted. The hardening work below applies when that changes.

## 3. Data classification

Classification drives every later decision (storage, logging, transmission,
retention). Sensitivity increases down the table.

| Data | State | Sensitivity | Leaves device today? | Notes |
| --- | --- | --- | --- | --- |
| Official Catalog / GreeneryInfo / CareGuide | Current | Public / non-personal | Read-only from mock | Reference content; never customer state |
| Local mock images | Current | Non-personal | No | Bundled demo assets; not user photos |
| My Greenery entries | Current | Personal (low–medium) | No | Names, locations, notes = personal context |
| Care Tasks / Logs | Current | Personal (low–medium) | No | Reveals routines / presence patterns |
| Consultation Drafts | Current | Personal (medium) | No | May contain free-text problem details |
| Future user photos | Future | Personal (high) | N/A (not implemented) | Sensitive content + EXIF/location risk |
| Future account / auth data | Future | Sensitive (high) | N/A | Identifiers, tokens, credentials |
| Future support / contact data | Future | Personal (medium–high) | N/A | Name, phone, email, address, message body |
| Future analytics data | Future (only if approved) | Personal if not aggregated | N/A | Default: not collected |

Guiding rule: **treat any free-text or user-provided field as potentially
containing personal information**, even when the schema does not require it.

## 4. Data minimization policy

- Collect the **minimum** data required for a feature to function; do not add
  fields "in case they are useful later."
- Prefer local computation over collection; prefer ephemeral over stored.
- Do not introduce identifiers (device IDs, advertising IDs, stable user IDs)
  without an explicit privacy decision.
- Keep free-text fields optional and add UI guidance discouraging over-sharing
  of sensitive details (already the stance for Consultation Drafts).
- Every new personal field must record: why it is collected, where it is
  stored, whether it is transmitted, and when it is deleted.

## 5. Local-first storage policy

- **Current:** customer data stays on-device; there is no backend to sync to.
- **Future:** remain local-only **until a real backend is explicitly approved**.
  Local-first is the default, not a temporary limitation.
- When on-device persistence is added, rely on OS-standard at-rest protection
  (iOS Data Protection / file protection classes; Android app-private storage,
  and encryption where the data is sensitive).
- Separate sensitivity tiers: non-sensitive cache vs. personal data vs. secrets.
  Secrets never go in plain `UserDefaults` / `SharedPreferences`.
- Review OS backup inclusion (iCloud/Android Auto Backup) per data class;
  exclude secrets and any data that should not be replicated off-device.

## 6. Secret / token handling policy

- **No secrets anywhere in the repo or app**: not in source, `Info.plist`,
  Gradle files, `UserDefaults`, `SharedPreferences`, mock data, docs, or logs.
- **Current:** there are no tokens because there is no real auth/API.
- **Future tokens/credentials:**
  - iOS: store in the **Keychain** (appropriate accessibility class, no
    unnecessary cloud sync).
  - Android: store via the **Keystore-backed** mechanism (e.g.
    EncryptedSharedPreferences / Keystore-wrapped keys).
- Never log tokens or embed them in URLs, analytics, or crash reports.
- Rotate and revoke on sign-out; do not persist beyond session need.
- Any API keys required for a real backend belong on the **server**, not shipped
  in the client.

## 7. Logging / redaction policy

- Do not log personal data (My Greenery notes, Care memos, Consultation Draft
  bodies, contact details) or secrets.
- Keep production logging minimal; verbose/debug logging must be disabled in
  release builds.
- When logging is necessary, **redact** identifiers and free-text; log stable
  non-personal keys/enums instead of raw content.
- No third-party log/telemetry pipeline without a privacy review (see §13).

## 8. Image / photo privacy policy

- **Mock images vs. user photos are strictly separated.** Mock/demo assets are
  bundled, allowlisted `mock://` references; the local mock rendering path must
  **never** be reused as a user-photo pipeline. Keep the two code paths distinct.
- **No real customer photos** in mock/demo assets. Demo imagery is brand-neutral
  and generated; it must not depict real customers, real sites, or real signage.
- **Current:** there is no user-photo capture, storage, or upload.
- **Future user photos** are **high-sensitivity** content:
  - Treat capture/selection as an explicit, permission-gated action.
  - **Strip EXIF / location / device metadata before any upload.**
  - Store locally with at-rest protection; do not sync without approval.
  - Never place user photos on the mock/demo path or in the repo.

## 9. Network / API hardening plan (Future)

Applies only when a real API is introduced:

- **HTTPS only**; never disable TLS validation or trust invalid certificates.
- Keep mock server strictly development-only; never mix production tokens into
  mock environments, and never point release builds at mock endpoints.
- Consider certificate pinning for sensitive endpoints (evaluate maintenance
  cost vs. benefit).
- Send the minimum necessary payload; never send data the server does not need.
- Handle errors without leaking sensitive detail to logs or UI.
- Production URLs and real contact data require **explicit approval** before
  introduction (none today).

## 10. Auth / session hardening plan (Future)

Applies only when real authentication is introduced (MVP may use mock auth):

- Use a vetted, standard auth flow; do not invent custom credential crypto.
- Store session tokens only in Keychain / Keystore-backed storage (§6).
- Enforce sensible session lifetime, refresh, and explicit sign-out that clears
  local tokens and sensitive caches.
- Do not embed credentials in code, URLs, or logs.
- Treat account identifiers as sensitive; minimize their exposure in UI/logs.

## 11. Server-side authorization expectations (Future backend)

- **Authorization must be enforced server-side.** Client-side checks are UX
  only and are **not** a security boundary.
- Every request touching personal data must verify the caller is authorized for
  that specific resource (no "authenticated == authorized").
- Enforce least privilege and per-user data isolation on the server.
- Validate and sanitize all input server-side; never trust the client.
- Keep official read-only content (Catalog/GreeneryInfo/CareGuide) genuinely
  read-only on the server; customer data must never be writable cross-user.

## 12. Privacy policy / iOS PrivacyInfo / Android Data Safety readiness (Future)

- Maintain an accurate, plain-language privacy policy that matches what the app
  actually collects — updated **before** any collecting feature ships.
- iOS: prepare a **Privacy Manifest (PrivacyInfo.xcprivacy)** declaring data
  types, purposes, and any required-reason APIs, kept in sync with real behavior.
- Android: prepare a **Data Safety** form that accurately reflects collection,
  sharing, and security practices.
- These artifacts must never over- or under-declare; they follow implementation,
  and no data-collecting feature is released without them current.

## 13. Analytics / tracking policy

- **Default: analytics and tracking are disabled / absent.** None exists today.
- Any analytics requires an **explicit privacy review and approval**, with a
  documented purpose, data classification, and retention decision.
- No advertising SDKs, no cross-app tracking, no stable advertising identifiers.
- Prefer aggregate, non-identifying, opt-in measurement if analytics is ever
  approved; never silently collect personal data.

## 14. Delete / export / retention policy (Future account features)

- **Current:** data is local; uninstalling the app removes it. Local
  Archive/Restore is a soft, reversible lifecycle control (no hard delete),
  and that remains a UX concept, not a server retention policy.
- **Future** (once data can leave the device):
  - Provide user-initiated **account/data deletion** and honor it server-side.
  - Provide **data export** where required by policy/regulation.
  - Define **retention windows** per data class; delete when no longer needed.
  - Ensure deletion propagates to backups and any derived copies.

## 15. Threat model summary

Assets: customer personal data (My Greenery, Care, Consultation Drafts), future
photos, future account/session tokens, future support/contact data.

| Threat | Relevance today | Future mitigation |
| --- | --- | --- |
| Device loss / local data exposure | Low (local, low-sensitivity) | OS at-rest protection; secrets in Keychain/Keystore; backup review |
| Secret leakage in repo/app/logs | Guarded (none today) | No secrets in client; redaction; server-held keys |
| Network interception | N/A (no network data) | HTTPS-only; no TLS bypass; optional pinning |
| Broken/missing server authorization | N/A (no backend) | Server-side authz; per-user isolation; least privilege |
| Photo metadata leakage (EXIF/location) | N/A (no photos) | Strip metadata before upload; sensitive-content handling |
| Over-collection / silent tracking | Guarded (none today) | Data minimization; analytics off by default + privacy review |
| Third-party SDK data exfiltration | N/A (none) | Vet SDKs; privacy review before adding any |

Trust boundaries to respect as the app grows: device ↔ network, client ↔
server, app ↔ third-party SDKs. Personal data crossing any boundary is a
decision requiring the checklist in §16.

## 16. Security checklist before enabling each future feature

Each feature must pass its checklist **before** shipping. All items assume the
generic gates: data classified (§3), minimized (§4), privacy policy + platform
declarations updated (§12), no secrets in client (§6), no personal data in logs
(§7).

**Real auth**
- [ ] Standard/vetted auth flow; no custom credential crypto
- [ ] Tokens in Keychain / Keystore-backed storage only
- [ ] Session lifetime, refresh, and sign-out clear tokens + sensitive caches
- [ ] No credentials in code, URLs, or logs

**Real API**
- [ ] HTTPS-only; TLS validation never disabled
- [ ] Minimal payloads; no unnecessary personal data sent
- [ ] Errors handled without leaking sensitive detail
- [ ] Production URLs explicitly approved; mock endpoints excluded from release

**Cloud sync**
- [ ] Explicit approval to move customer data off-device
- [ ] Server-side authorization + per-user isolation verified
- [ ] Conflict/retention behavior defined; deletion propagates
- [ ] At-rest + in-transit protection confirmed

**Image upload**
- [ ] Permission-gated capture/selection with clear purpose
- [ ] **EXIF/location/device metadata stripped before upload**
- [ ] User photos kept off the mock/demo path and out of the repo
- [ ] Sensitive-content storage + transmission protections confirmed

**Support submission**
- [ ] Explicit approval to transmit contact/message data (today it is phone
      guidance only, nothing submitted)
- [ ] Minimal fields; UI discourages over-sharing sensitive details
- [ ] Server-side authorization + validation; secure transport
- [ ] Clear user expectation of what is sent and to whom

**Notifications**
- [ ] Opt-in; purpose limited (e.g., care reminders)
- [ ] No personal/sensitive content in notification payloads or logs
- [ ] No use as a covert tracking/telemetry channel

**Analytics**
- [ ] Explicit privacy review + approval (default is off)
- [ ] No advertising/tracking identifiers; no cross-app tracking
- [ ] Aggregate/opt-in where possible; purpose + retention documented
- [ ] Platform Data Safety / Privacy Manifest updated to match

## 17. Explicitly out of scope for the current MVP

This plan does **not** add, and the MVP does not include:

- Real authentication, real API, cloud sync, or cloud storage
- Image upload, camera / photo-library access, file picker, or storage permissions
- Analytics, tracking, crash-reporting, or ad SDKs
- Production URLs, secrets, tokens, or real customer/company/contact data
- Support submission, chat, ticketing, scheduling, orders, estimates, or payment
- Staff/admin consoles or hard-delete of customer data
- Any change to app code, mock data, server/mock-api behavior, schema, models,
  ViewModels, UseCases, Repositories, permissions, or release/signing settings

## 18. Recommended phased implementation order

Order chosen so foundational protections exist before data is collected or moved:

1. **Foundations (docs → light infra):** secret-handling discipline, logging/
   redaction rules, data-classification review — no new data collection.
2. **On-device protection:** at-rest protection for local personal data, backup
   review, secure storage primitives ready (before any secrets exist).
3. **Auth (mock → real):** session/token handling via Keychain/Keystore, sign-out
   hygiene; keep data local.
4. **Real API (read-only first):** HTTPS-only client, minimal payloads, server-
   side authorization for any personal endpoint.
5. **Cloud sync of customer data:** only after §16 cloud-sync gate; per-user
   isolation, retention, and deletion propagation.
6. **Image upload:** permission-gated, EXIF/metadata stripping, sensitive-content
   handling, kept off the mock/demo path.
7. **Support submission:** explicit approval, minimal fields, secure transport.
8. **Notifications:** opt-in care reminders with no sensitive payloads.
9. **Analytics (optional, last):** only if approved via privacy review; off by
   default.

At every step: update the privacy policy, iOS Privacy Manifest, and Android Data
Safety declarations to match actual behavior, and keep current MVP state clearly
separated from planned future work.
