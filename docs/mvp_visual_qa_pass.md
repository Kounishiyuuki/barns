# MVP Visual QA Pass — Local Mock Image Integration

A focused QA + low-risk UI polish pass after local mock image integration
(PR #77 assets, PR #78 rendering). Local-first / mock-first; no real
auth/API/sync/upload/analytics. See also
[Device QA Checklist](device_qa_checklist.md) and
[Mock Image Asset Plan](mock_image_asset_plan.md).

> Scope: presentation-only polish. No schema/model/Repository/UseCase/
> persistence/server changes. No new features, no network image loading, no new
> assets, no permissions.

## 1. QA method (what was actually run)

- Code review of Catalog list/detail, My Greenery list/detail, the shared local
  mock image helper, image fallback/null paths, captions, and Home/Support/
  Care/Settings entry wording, on **both iOS and Android**.
- iOS build + unit tests: **passed**.
- Android build + unit tests: **passed**.
- iOS app **launched in the Simulator** and screenshotted at the mock-auth
  landing screen: renders cleanly, no broken layout.
- **Not run:** in-app navigation to Catalog / My Greenery / detail heroes /
  fallback rows was **not** visually verified. The environment has no tap
  automation (no `cliclick` / `idb`; System Events lacks accessibility
  permission), so screens behind the first tap could not be driven. Those items
  remain open for manual QA (see §5).

## 2. Findings

| # | Area | Finding | Action |
| --- | --- | --- | --- |
| 1 | Detail hero shape | iOS detail heroes were full-bleed with square corners; Android heroes were inset with rounded corners (12dp). Cross-platform inconsistency. | Fixed iOS to a rounded (12) inset hero to match Android. |
| 2 | List thumbnails | iOS and Android already consistent (56pt, corner radius 8, fill/crop). | No change. |
| 3 | Fallback (catalog maintenance-kit, no image) | List shows a neutral placeholder; detail shows no hero and no empty gap. Safe. | No change (verified in code). |
| 4 | Captions | Detail captions read as mock/demo ("Not a real product/customer photo"). | No change. |
| 5 | Read-only / local wording | Catalog reads as official read-only reference; My Greenery reads as local registry; Home entry wording ("Explore official catalog" / "Official read-only reference") consistent across platforms. | No change. |
| 6 | Placeholder glyph | iOS list placeholder shows a subtle leaf glyph; Android placeholder is a plain neutral box. Minor, appears only on the one image-less catalog row. | Accepted minor difference (avoids adding an icon dependency on Android). |

## 3. Changes applied

- **iOS Catalog detail** and **iOS My Greenery detail**: hero image now uses
  `clipShape(RoundedRectangle(cornerRadius: 12))` with default (inset) list row
  insets, replacing the previous full-bleed square-cornered treatment. This
  matches the Android rounded, inset hero for a calm, consistent look.
- No Android code change was needed (it already matched the target).

## 4. Boundaries preserved

- `mock://` resolution remains **exact allowlisted category + asset-name** only;
  `nil` / unknown / `http(s)` references remain safe (no load, no crash).
- No network image loading, no remote URLs, no new assets, no new dependencies.
- Catalog stays official read-only reference; My Greenery stays local
  customer-owned registry. Edit / Archive / Restore behavior unchanged.
- No implication of uploaded real customer photos, cloud sync, EC/ordering, or
  support submission.

## 5. Open manual QA items (not verified in this environment)

Run on a booted iOS Simulator and Android emulator/device:

- [ ] Catalog list: thumbnails and the image-less maintenance-kit placeholder
  render with consistent spacing
- [ ] Catalog detail: rounded hero aspect ratio/clipping; caption; null-image
  case shows no hero and no gap
- [ ] My Greenery list: thumbnails, location/note readability, spacing
- [ ] My Greenery detail: rounded hero + caption; edit/archive/restore intact
- [ ] Large-text / accessibility: images stay decorative and do not block row
  text or VoiceOver / TalkBack reading
- [ ] Airplane mode: image rendering unaffected (confirms no network path)
