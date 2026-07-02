# Android Data Safety Draft (Google Play)

A **draft** of the Google Play Data Safety answers for the current local-first /
mock-first MVP. Docs-only reference. See also
[Release Readiness Checklist](release_readiness_checklist.md),
[Security & Privacy Hardening Plan](security_privacy_hardening_plan.md), and
[05_security_privacy.md](05_security_privacy.md).

> This is a **draft** for the current MVP only. It is **not** a submitted Data
> Safety form, **not** final legal/privacy approval, and does **not** assert
> Google Play readiness. Re-verify before any store submission.

## 1. Permissions audit (current MVP)

The app's source `AndroidManifest.xml` declares **no** runtime or dangerous
permissions:

- No `INTERNET` declared by the app (no networking in the MVP).
- No camera / photo / media / storage permissions.
- No location, contacts, microphone, or phone permissions.
- The only entry in the *merged* manifest is a build-tool signature permission
  (`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`) added automatically for debug
  builds; it is not an app-declared capability.

Do **not** add permissions to satisfy this draft.

## 2. Draft Data Safety answers

| Question | Draft answer (current MVP) |
| --- | --- |
| Does your app collect or share any of the required user data types? | **No** — all app data (My Greenery, Care Tasks/Logs, Consultation Drafts) stays on the device. |
| Is all user data encrypted in transit? | **N/A** — no data is transmitted; there is no backend. |
| Do you provide a way for users to request data deletion? | Local only — items use soft Archive/Restore (no hard delete); nothing leaves the device. |
| Data collected | **None** (off-device). |
| Data shared | **None**. |
| Analytics / tracking | **None** — no analytics or tracking SDK. |
| Advertising / advertising ID | **None**. |
| Account / authentication | **None** — no account; mock guest entry only. |
| Photos / media upload | **None** — no image upload; images are local mock/demo assets, not user photos. |

## 3. What the MVP actually contains

- Local **My Greenery** registry and local Register / Edit / Archive / Restore
  lifecycle (soft status only; no hard delete).
- Local **Care Tasks / Care Logs** (on-device records; no reminders/sync).
- Official **read-only** Catalog / GreeneryInfo / CareGuide reference content.
- Local **Consultation Draft** preparation — never submitted.
- **Local mock images only** in Catalog / My Greenery, resolved by an exact
  `mock://` allowlist with no network loading. Not real customer/company photos.

## 4. Must be re-verified before store submission

- [ ] Re-audit `AndroidManifest.xml` for any added permissions.
- [ ] If networking/analytics/auth/upload is added later, redo this form and
      route the change through the
      [Security & Privacy Hardening Plan](security_privacy_hardening_plan.md).
- [ ] Confirm no bundled dependency introduces data collection needing
      disclosure.
- [ ] Legal/privacy copy and the final Data Safety form are reviewed and
      approved (not covered here).

## 5. What this is not

- Not a submitted Data Safety declaration.
- Not a privacy policy or legal approval.
- Not a claim that the app is production- or Play-ready.
