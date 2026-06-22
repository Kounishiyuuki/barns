import Foundation

/// Presentation-only models for the supporting Catalog experience.
///
/// Boundary: built from official read-only content (`CatalogItem`,
/// `GreeneryInfo`, `CareGuide`) only. No customer-owned `ProductItem` state,
/// no pricing/stock/order fields, nothing submitted. Catalog is supporting
/// reference content, not a shopping flow.

/// A concise Catalog list row.
struct CatalogPresentationItem: Equatable, Identifiable {
    let id: String
    let name: String
    let kindLabel: String
    let summary: String

    init(item: CatalogItem) {
        id = item.id
        name = item.name
        kindLabel = CatalogKind.label(for: item.kind)
        summary = item.summary
    }
}

/// Concise Catalog detail content, with optional linked official info.
struct CatalogDetailContent: Equatable {
    struct CareGuideSummary: Equatable, Identifiable {
        let id: String
        let title: String
        let summary: String
    }

    let name: String
    let kindLabel: String
    let summary: String
    let overview: String?
    let lightPreference: String?
    let wateringOverview: String?
    let careGuides: [CareGuideSummary]
    /// Prefill for starting a local Register Greenery flow from this item.
    /// Built from official fields only; the user still confirms and saves.
    let registerPrefill: RegisterGreeneryPrefill

    var hasBasicInformation: Bool {
        overview != nil || lightPreference != nil || wateringOverview != nil
    }

    var hasCareGuides: Bool { !careGuides.isEmpty }
}

/// Humanizes a `CatalogItem.kind` token (e.g. "wall-greening") for display.
enum CatalogKind {
    static func label(for kind: String) -> String {
        let words = kind.split(separator: "-").map(String.init)
        guard let first = words.first, !first.isEmpty else { return "Greenery" }
        let rest = words.dropFirst().joined(separator: " ")
        let label = first.capitalized + (rest.isEmpty ? "" : " " + rest)
        return label.isEmpty ? "Greenery" : label
    }
}
