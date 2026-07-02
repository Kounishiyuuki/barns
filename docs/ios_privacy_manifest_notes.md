# iOS Privacy Manifest Notes (`PrivacyInfo.xcprivacy`)

Notes behind the app's iOS privacy manifest. Docs-only reference. The manifest
itself lives at `apps/ios/Barns/BarnsApp/Resources/PrivacyInfo.xcprivacy` and is
bundled with the app target. See also
[Security & Privacy Hardening Plan](security_privacy_hardening_plan.md),
[Release Readiness Checklist](release_readiness_checklist.md), and
[05_security_privacy.md](05_security_privacy.md).

> This documents the **current local-first / mock-first MVP** only. It is **not**
> a final privacy/legal review and does **not** assert App Store readiness. The
> manifest must be re-audited whenever real data handling is added.

## 1. What the manifest declares (and why)

| Key | Value | Why |
| --- | --- | --- |
| `NSPrivacyTracking` | `false` | No tracking, no cross-app/website tracking, no ad SDKs. |
| `NSPrivacyTrackingDomains` | empty | No tracking, so no domains to list. |
| `NSPrivacyCollectedDataTypes` | empty | Nothing is collected/transmitted off device. My Greenery, Care, and Consultation Drafts are on-device only; there is no backend, account, or upload. |
| `NSPrivacyAccessedAPITypes` | empty | The audit found no required-reason API usage (see §2). |

Apple defines "collect" as transmitting data off the device. The current MVP
transmits nothing, so no data types are declared. This is intentionally
**not over-disclosed** — declaring collection that does not happen would be
inaccurate.

## 2. Required-reason API audit (current MVP)

A code audit of the iOS target found **none** of the common required-reason
APIs in use, so `NSPrivacyAccessedAPITypes` is empty:

- No `UserDefaults` usage (no `NSPrivacyAccessedAPICategoryUserDefaults`).
- No `FileManager` / file-timestamp / disk-space APIs.
- No `NSKeyedArchiver` / Core Data / SQLite / Keychain.
- No `URLSession` / networking of any kind.
- Repositories are in-memory mock actors; nothing persists off-device.

If any of these are introduced later (e.g. `UserDefaults` for local persistence),
the corresponding required-reason entry **must** be added with an accurate reason
code.

## 3. Must be re-verified before store submission

This manifest is accurate for the current MVP but is **not** a substitute for a
release-time review. Before any TestFlight / App Store submission:

- [ ] Re-run the required-reason API audit against the then-current code.
- [ ] If persistence (e.g. `UserDefaults`, files) is added, declare it accurately.
- [ ] If any SDK/dependency is added, check its own privacy manifest and update.
- [ ] If real auth/API/sync/image upload is added, revisit
      `NSPrivacyCollectedDataTypes` and route the change through the
      [Security & Privacy Hardening Plan](security_privacy_hardening_plan.md).
- [ ] Confirm the file is included in the app target's bundle resources.
- [ ] Legal/privacy copy and App Privacy answers in App Store Connect are
      reviewed and approved (not covered here).

## 4. What this is not

- Not a privacy policy and not legal approval.
- Not an App Store "App Privacy" questionnaire answer (that is filled in App
  Store Connect at submission time).
- Not a claim that the app is production- or store-ready.
