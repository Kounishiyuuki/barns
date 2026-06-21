/// Temporary mapping from a registered greenery's `categoryId` to the official
/// read-only content (basic info + default care guides) that should be shown
/// as supporting after-care reference.
///
/// Why this exists: `ProductItem` does not yet carry an explicit
/// `greeneryInfoId` / `catalogItemId`. Rather than rewrite the model now, this
/// resolver provides a small, documented, presentation-layer link by category.
/// The ids mirror shared/mock-data and the official content repositories. When
/// `ProductItem` gains an explicit official-content id, replace this resolver.
///
/// Boundary: pure value mapping only. No customer-owned state, no I/O.
enum OfficialContentLink {
    struct Resolved: Equatable {
        let greeneryInfoId: String?
        let careGuideIds: [String]
    }

    static func resolve(categoryId: String) -> Resolved {
        switch categoryId {
        case "cat-wall-green":
            return Resolved(
                greeneryInfoId: "greenery-info-wall-green",
                careGuideIds: ["guide-wall-green-basic", "guide-cleaning-basic"]
            )
        case "cat-interior-green":
            return Resolved(
                greeneryInfoId: "greenery-info-interior-foliage",
                careGuideIds: ["guide-watering-basic", "guide-planter-sunlight-basic"]
            )
        case "cat-maintenance-supply":
            return Resolved(
                greeneryInfoId: nil,
                careGuideIds: ["guide-cleaning-basic"]
            )
        default:
            return Resolved(greeneryInfoId: nil, careGuideIds: [])
        }
    }
}
