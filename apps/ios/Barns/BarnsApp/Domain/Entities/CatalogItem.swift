import Foundation

/// Official, read-only catalog content: products / installation / greenery
/// candidates shown as supporting reference, not customer-owned state.
///
/// Boundary: this is shared official content (same for every user). It must
/// never carry user-owned `ProductItem` state — no location label, personal
/// notes, ownership status, care logs, or consultation drafts. It may only
/// reference official content by id (`greeneryInfoId`, `careGuideIds`).
struct CatalogItem: Equatable, Identifiable, Sendable {
    let id: String
    let categoryId: String
    let name: String
    let kind: String
    let summary: String
    let greeneryInfoId: String?
    let careGuideIds: [String]
    let imageUrl: URL?
}
