import Foundation

/// Archives a registered greenery locally by setting its status to `.archived`.
/// This is a soft action — the item is kept in the local store (no hard
/// delete) and all official content links (categoryId, careGuideIds, imageUrl)
/// and other fields are preserved. Local-only: never sent to a server.
struct ArchiveProductItemUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute(_ item: ProductItem) async throws -> ProductItem {
        let archived = ProductItem(
            id: item.id,
            categoryId: item.categoryId,
            name: item.name,
            type: item.type,
            installedOrPurchasedAt: item.installedOrPurchasedAt,
            locationLabel: item.locationLabel,
            status: .archived,
            careGuideIds: item.careGuideIds,
            notes: item.notes,
            imageUrl: item.imageUrl,
            updatedAt: Date()
        )
        try await repository.updateProductItem(archived)
        return archived
    }
}
