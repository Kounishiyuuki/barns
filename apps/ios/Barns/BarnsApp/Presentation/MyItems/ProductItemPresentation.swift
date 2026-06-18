import Foundation

/// Presentation-only display helper that frames a registered `ProductItem`
/// as the customer's own/installed greenery — closer to a "My Appliances for
/// greenery" registry than a generic care list.
///
/// This is a pure value type derived from existing domain fields only. It adds
/// no new domain model, no persistence, and no data access; it just shapes
/// calm, support-oriented copy for the My Items list and detail screens.
struct ProductItemPresentation: Equatable {
    let name: String
    let categoryLabel: String
    let typeLabel: String
    let locationLabel: String
    let statusLabel: String
    let careStatusLabel: String
    let nextActionHint: String
    let ownershipSummary: String

    init(item: ProductItem) {
        name = item.name
        categoryLabel = Self.makeCategoryLabel(for: item.categoryId)
        typeLabel = Self.makeTypeLabel(for: item.type)
        locationLabel = item.locationLabel ?? "Location not set"

        switch item.status {
        case .active:
            statusLabel = "In your care"
        case .archived:
            statusLabel = "Archived"
        }

        if item.careGuideIds.isEmpty {
            careStatusLabel = "No care guide linked yet"
            nextActionHint = "Browse care guidance to set up routine care"
        } else {
            careStatusLabel = "Care guidance linked"
            nextActionHint = "Open care guidance for your next step"
        }

        ownershipSummary = Self.makeOwnershipSummary(for: item.type)
    }

    private static func makeCategoryLabel(for categoryId: String) -> String {
        switch categoryId {
        case "cat-wall-green":
            return "Wall greenery"
        case "cat-interior-green":
            return "Interior greenery"
        default:
            // Fall back to a readable label derived from the id, e.g.
            // "cat-roof-garden" -> "Roof garden".
            let trimmed = categoryId.hasPrefix("cat-")
                ? String(categoryId.dropFirst("cat-".count))
                : categoryId
            let words = trimmed.split(separator: "-").map(String.init)
            guard let first = words.first, !first.isEmpty else { return "Greenery" }
            let rest = words.dropFirst().joined(separator: " ")
            let label = (first.capitalized + (rest.isEmpty ? "" : " " + rest))
            return label.isEmpty ? "Greenery" : label
        }
    }

    private static func makeTypeLabel(for type: ProductItemType) -> String {
        switch type {
        case .installed:
            return "Installed greenery"
        case .purchased:
            return "Owned greenery"
        }
    }

    private static func makeOwnershipSummary(for type: ProductItemType) -> String {
        switch type {
        case .installed:
            return "Installed greenery in your care"
        case .purchased:
            return "Greenery you own"
        }
    }
}
