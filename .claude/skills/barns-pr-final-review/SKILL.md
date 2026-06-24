---
name: barns-pr-final-review
description: Standard final-review checklist for barns Pull Requests before merge. Use when reviewing a barns PR so review prompts can stay short. Review only — never modify, commit, push, open PRs, or merge; report issues instead of fixing them.
---

# barns-pr-final-review

A reusable final-review policy for barns Pull Requests. It captures the standard
review categories so a review prompt can be one or two lines. Use it before a barns
PR is merged.

## Hard rules (review only)

- **Review only.** Do **not** modify files.
- Do **not** commit, push, create PRs, or merge.
- Do **not** run destructive commands (no `git reset --hard`, no force push, no branch
  deletion, no file deletion, no writes).
- Read-only inspection is fine: `git diff`, `git log`, `gh pr view`, `gh pr diff`,
  `gh pr checks`, `git diff --check`, grep/search.
- If you find issues, **report them — do not fix them.** The author fixes and asks for
  re-review.

## Product context (for judging scope)

- barns is a "Panasonic My Appliances app for gardening / interior greenery" style
  **after-support app**.
- **My Greenery** is the primary customer-owned registry.
- **Catalog** is supporting official **read-only** reference content.
- Prefer performance, clarity, stability, and high-quality UX over feature count.
- Avoid unnecessary feature bloat. Features the product owner explicitly requests are
  valid future candidates, but should be implemented in safe phases (docs → local/mock
  → iOS → Android parity → QA/docs → real API/auth/sync only when approved).

## Review categories

### 1. Scope check
- Changed files match the PR's stated purpose.
- No unrelated cleanup.
- No generated / build / IDE / temp / `local.properties` files.
- No dependency, signing, bundle/package, CI, server, mock-data, or platform changes
  unless explicitly intended.
- Docs-only PRs stay docs-only.
- iOS-only PRs do not touch Android.
- Android-only PRs do not touch iOS unless explicitly justified.

### 2. Product direction check
- Stays an after-support experience; My Greenery remains primary.
- Catalog stays supporting read-only reference, not shopping / EC.
- No unnecessary feature bloat; quality over feature count.
- Explicitly requested features are phased, not dumped into one PR.

### 3. Architecture check
- Preserves Presentation (View / Screen) → ViewModel → UseCase → Repository interface.
- Views / Screens do not access repositories, JSON, mock data sources, network, or
  storage directly.
- Domain does not depend on SwiftUI / UIKit / Compose / Android framework / network /
  storage.
- No broad rewrites or cross-layer shortcuts.

### 4. Data boundary check
- `ProductItem` is customer-owned, local-first data.
- `CatalogItem` / `GreeneryInfo` / `CareGuide` are official read-only reference content.
- `RegisterGreeneryPrefill` is a UI bridge only — not persisted coupling.
- `ConsultationDraft` stays local-only and not submitted unless explicitly approved.
- Archive / Restore are soft status updates (`active` ↔ `archived`).
- No hard delete unless explicitly approved.
- No customer data sent anywhere unless explicitly approved.

### 5. Feature guardrails
- No real API / auth / sync unless explicitly approved.
- No production endpoints or secrets.
- No analytics / tracking.
- No image upload.
- No chat / scheduling / order / estimate / payment by default.
- No staff / admin by default.
- No real customer / company data, real addresses, real phone numbers, or real images.

### 6. UI review check (when relevant)
- UI stays calm, premium, nature-oriented, trustworthy.
- My Greenery stays visually and functionally primary.
- Catalog does not overpower the owned registry.
- Primary and secondary actions are clear.
- Archive is cautious / destructive-like but not a hard delete.
- Restore is clear and safe.
- Avoid visual clutter, heavy animations, unnecessary decoration.
- Preserve existing behavior unless the PR explicitly intends otherwise.

### 7. Performance check
- No expensive startup work.
- No unnecessary global state.
- No heavy image / media assumptions.
- No unnecessary network dependency.
- No broad visual / architecture rewrite.
- No new dependencies unless explicitly justified.

### 8. Test / validation check
- Relevant build/test command was run, or there is a clear reason it was not required.
- `git diff --check` is clean.
- Changed-files scope is confirmed.
- Secret / privacy grep is clean.
- No generated / build outputs added.
- GitHub Actions status checked when available.
- Docs-only PRs: app builds not required unless app/test files changed.
- iOS implementation PRs: iOS tests / CI should pass.
- Android implementation PRs: Android tests / CI should pass.
- Cross-platform CI should pass or be unaffected.

### 9. Security / privacy check
- No secrets, credentials, tokens, API keys, or production URLs.
- No analytics / tracking.
- No real customer / company data.
- No real phone numbers, real addresses, or real images.
- Sales / EC / order / payment / estimate terms appear only as guardrails / out-of-scope
  text unless explicitly approved.

## Output format (Japanese)

Report in Japanese with these sections:

- **結論**: mergeしてよい / 修正推奨 / merge不可
- **重要な指摘**
- **軽微な指摘**
- **scope確認**
- **product direction確認**
- **architecture/data boundary確認**
- **feature guardrails確認**
- **UI確認**（該当する場合）
- **performance確認**
- **tests/validation確認**
- **セキュリティ・プライバシー確認**
- **changed-files範囲確認**
- **build/test/CI確認**
- **merge前に必要な対応**

## Severity rules

**merge不可** (block):
- Security / privacy leak
- Destructive behavior
- Hard delete introduced without approval
- Real API / auth / sync introduced without approval
- Data boundary violation
- Build / test failure
- Unrelated broad rewrite
- App behavior change outside scope

**修正推奨** (fix recommended):
- Inaccurate docs
- Missing validation
- Questionable UI wording
- Minor scope creep
- CI pending when merge should wait

**mergeしてよい** (ok to merge):
- Scope is correct
- Validation passes
- CI passes or is appropriately unaffected
- No blocking issues

## Example short prompts

**Docs-only PR review**
```
Use barns-pr-final-review.
Review PR #54 for barns.
Focus: docs-only policy addition.
Review only. Do not modify files.
Output in Japanese.
```

**iOS UI PR review**
```
Use barns-pr-final-review.
Review PR #56 for barns.
Focus: iOS-only UI consistency pass.
Review only. Do not modify files.
Output in Japanese.
```

**Android UI PR review**
```
Use barns-pr-final-review.
Review PR #<n> for barns.
Focus: Android-only UI consistency pass; confirm iOS untouched.
Review only. Do not modify files.
Output in Japanese.
```

**Post-fix re-review**
```
Use barns-pr-final-review.
Re-review PR #<n> for barns after fixes.
Focus: confirm prior 重要な指摘 are resolved; recheck scope and CI.
Review only. Do not modify files.
Output in Japanese.
```
