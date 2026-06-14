import Foundation

struct ProductItem: Equatable, Identifiable, Sendable {
    let id: String
    let categoryId: String
    let name: String
    let type: ProductItemType
    let installedOrPurchasedAt: Date?
    let locationLabel: String?
    let status: ProductItemStatus
    let careGuideIds: [String]
    let notes: String?
    let imageUrl: URL?
    let updatedAt: Date?
}

enum ProductItemType: String, Equatable, Sendable {
    case installed
    case purchased
}

enum ProductItemStatus: String, Equatable, Sendable {
    case active
    case archived
}
