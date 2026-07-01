import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

/// Resolves and renders generated **local mock** demo images that are bundled
/// with the app. These are brand-neutral mock/demo assets only — never real
/// customer photos, production company photos, or anything ordered/submitted.
///
/// References use a `mock://<category>/<asset-name>` scheme stored in the
/// optional `imageUrl` field of existing models. No network loading ever
/// happens: unknown or `nil` references resolve to no image so image-null
/// states stay safe.
enum LocalMockImage {
    private static let allowedAssetsByCategory: [String: Set<String>] = [
        "catalog": [
            "catalog-office-vertical-green-wall-01",
            "catalog-reception-greenery-wall-01",
            "catalog-compact-framed-moss-panel-01",
        ],
        "my-greenery": [
            "my-greenery-entryway-green-wall-01",
            "my-greenery-reception-foliage-planter-01",
        ],
    ]

    /// Extracts the bundled asset base name from an allowlisted `mock://…`
    /// reference. Returns `nil` for `nil`, non-`mock` schemes (e.g. http),
    /// unknown categories, missing names, or category/name mismatches.
    static func assetName(for reference: URL?) -> String? {
        guard let reference,
              reference.scheme == "mock",
              let category = reference.host,
              let allowedAssets = allowedAssetsByCategory[category],
              reference.pathComponents.count == 2 else { return nil }
        let name = reference.lastPathComponent
        return allowedAssets.contains(name) ? name : nil
    }

    #if canImport(UIKit)
    /// Loads the bundled JPEG for a local mock reference, or `nil` if the
    /// reference is unknown / unmapped / not present in the bundle.
    static func uiImage(for reference: URL?) -> UIImage? {
        guard let name = assetName(for: reference),
              let url = Bundle.main.url(forResource: name, withExtension: "jpg"),
              let data = try? Data(contentsOf: url) else { return nil }
        return UIImage(data: data)
    }
    #endif
}

/// A small, reusable view for local mock demo imagery in Catalog and My
/// Greenery surfaces. Decorative by design (the surrounding text already names
/// the item), so it is hidden from accessibility. Sizing is controlled by the
/// caller via `.frame(...)`.
struct LocalMockImageView: View {
    let reference: URL?
    /// When `true`, a neutral placeholder keeps list-row layout stable for
    /// items without a mapped mock image. Detail heroes pass `false` and are
    /// only shown when an image actually resolves.
    var showsPlaceholder: Bool = true

    var body: some View {
        #if canImport(UIKit)
        if let image = LocalMockImage.uiImage(for: reference) {
            Image(uiImage: image)
                .resizable()
                .scaledToFill()
                .accessibilityHidden(true)
        } else if showsPlaceholder {
            placeholder
        }
        #else
        if showsPlaceholder { placeholder }
        #endif
    }

    private var placeholder: some View {
        Rectangle()
            .fill(Color(uiColor: .secondarySystemBackground))
            .overlay(
                Image(systemName: "leaf")
                    .imageScale(.small)
                    .foregroundStyle(.tertiary)
            )
            .accessibilityHidden(true)
    }
}
