# MVP UI Excellence Pass — Final Polish

A final, high-quality UI polish pass over the existing MVP, after local mock
image integration ([MVP Visual QA Pass](mvp_visual_qa_pass.md)). The goal is a
calm, premium, trustworthy after-support feel — not new features. Local-first /
mock-first; no real auth/API/sync/upload/analytics. See also
[Device QA Checklist](device_qa_checklist.md) and
[UI Consistency Checklist](ui_consistency_checklist.md).

> Scope: presentation-only polish. No schema/model/Repository/UseCase/
> persistence/server changes. No new features, no network image loading, no new
> assets, no permissions, no dependencies.

## 1. Method

- Full UI audit of both platforms across Home, My Greenery (list/detail/edit/
  archive/restore), Catalog (list/detail), Register, Care (list/detail/logs),
  Support / Consultation Draft / Phone guidance, Settings / Legal / Privacy,
  and empty / fallback / null-image states.
- Cross-platform consistency audit (iOS SwiftUI `List` vs Android Compose
  `Column`/`LazyColumn`), respecting native conventions.
- iOS build + unit tests: **passed** (65 tests). Android build + unit tests:
  **passed**.
- iOS app launched in the Simulator; mock-auth landing screenshotted, renders
  cleanly with no crash.
- **Not run:** automated in-app navigation to each screen. The environment has
  no tap automation (no `cliclick` / `idb`; System Events lacks accessibility
  permission), so screens behind the first tap were verified by code review,
  not visually driven. See §5 for the manual QA that remains.

## 2. What felt weak before

| # | Platform | Area | Problem |
| --- | --- | --- | --- |
| 1 | Android | Home | Content was a non-scrolling `Column`; at large font scale / short screens the rows could overflow and clip with no scroll (every other screen already scrolls). |
| 2 | Android | My Greenery list / Catalog list | A `LazyColumn` with no `weight` inside a `Column` consumed all remaining height, pushing the trailing "stays on this device" / "no ordering" reassurance footer off-screen. |
| 3 | Android | Empty states | My Greenery / Catalog empties were two bare left-aligned `Text`s — inconsistent and less reassuring than the iOS `ContentUnavailableView`. |
| 4 | iOS | Settings | Long About / Privacy details were rendered as a right-aligned trailing `LabeledContent` value, squeezed into ~half the row width and hard to read. |

## 3. What was improved

- **Android Home** now scrolls (`verticalScroll`), matching Care / Settings /
  detail screens and staying resilient to large text and short screens.
- **Android My Greenery & Catalog lists** give the `LazyColumn`
  `Modifier.weight(1f)` so the reassurance footer stays visible and the layout
  is correct. The Catalog empty case no longer renders an empty list + footer.
- **New shared `EmptyState` component** (Android `presentation/common`) gives
  the full-screen My Greenery and Catalog empties a calm, centered title +
  message, closer to the iOS `ContentUnavailableView` treatment.
- **iOS Settings** rows now stack the title over a full-width secondary detail,
  so long notes wrap naturally and read clearly — and this mirrors the Android
  headline / supporting layout for cross-platform parity.

## 4. Intentionally left unchanged (and why)

- **iOS lists / detail heroes** — already polished (PR #80); rounded inset
  heroes, native `List`/`Section`/`ContentUnavailableView`. No churn added.
- **Android detail heroes** — already rounded, inset, captioned. No change.
- **Care per-section inline empties** ("No upcoming care tasks…", "No care
  logged yet…") — correct as secondary text under their section headers; a
  centered full-screen empty state would look wrong mid-list.
- **Navigation pattern** (Android in-screen `Back` buttons vs iOS
  `NavigationStack`) — converting to `Scaffold`/`TopAppBar` app-wide is a broad,
  risky refactor and out of scope for a polish pass.
- **iOS Home count row** — the trailing count is an idiomatic iOS list style and
  reads clearly; left native.
- **Wording / boundaries** — local-only / read-only / not-submitted copy was
  already aligned in earlier PRs; no wording changes were needed.

## 5. Still needs real-device manual QA

Run on a booted iOS Simulator and an Android emulator/device:

- [ ] Android Home scrolls fully at the largest font scale with no clipped rows
- [ ] Android My Greenery / Catalog list footer reassurance text is visible
      below the scrolling list
- [ ] Android My Greenery / Catalog empty states read as calm, centered states
- [ ] iOS Settings long details wrap full-width and read clearly (Dynamic Type)
- [ ] Catalog / My Greenery detail heroes and captions still render consistently
- [ ] Fallback / null image (catalog maintenance kit): neutral placeholder in
      lists, no hero and no gap in detail
- [ ] VoiceOver / TalkBack: list rows read as one clear element; images stay
      decorative; Settings rows read title + detail together

## 6. Why this is meaningfully more demo-ready

The changes remove two real Android layout defects (non-scrolling Home,
off-screen list footers), lift the weakest surfaces (Android empty states, iOS
Settings readability) to match the rest of the app, and tighten cross-platform
parity — while preserving every local-first / mock-only / read-only /
not-submitted boundary and all `mock://` allowlist / null-image safety.
