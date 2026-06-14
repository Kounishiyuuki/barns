---
name: barns-security-privacy-review
description: Security and privacy review checklist for barns changes. Use before committing or when reviewing changes that touch data storage, networking, auth, secrets, or customer data.
---

# barns-security-privacy-review

Security and privacy must be considered from the beginning of barns. This skill is a review checklist applied to changes, especially those touching data, networking, auth, or secrets.

## When to use

- Before committing changes that touch data storage, networking, authentication, or configuration.
- When reviewing a diff for privacy/security risks.
- When adding any call to the mock (or future) server.

## Rules

**Data minimization & local-first:**
- Customer-side data stays local-first. Verify personal data is not transmitted.
- Confirm none of these leave the device to the mock server: registered items, care logs, consultation drafts, photos, addresses, phone numbers, personal notes.
- Confirm mock-server traffic is limited to read-only: categories, patterns, care guides, notices, company info.

**Secrets & config:**
- No secrets, API keys, `.env`, or `.dev.vars` committed. Check the diff and staged files.
- No hardcoded credentials or tokens in source.

**Content safety:**
- No real customer data, real addresses, real phone numbers, or unauthorized images. Placeholders must be obviously fake.

**Auth:**
- MVP may use mock authentication; ensure it is clearly mock and not a false sense of real security.
- Do not log sensitive data.

**External contact:**
- External inquiries are phone consultation guidance only — no in-app submission of personal data to external services.

## Output expectations

- A short pass/fail review noting any leaks of personal data, secrets, or out-of-scope transmissions.
- Confirmation that committed files contain no secrets or real PII.
- Any risk is flagged with the specific file/line; clean changes are stated plainly.
