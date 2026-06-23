import Foundation

/// Restores an archived greenery locally by setting its status back to
/// `.active`. This is the inverse of `ArchiveProductItemUseCase`: it is a soft
/// action — the item already lives in the local store (no hard delete, no
/// re-insertion) and all official content links (categoryId, careGuideIds,
/// imageUrl) and other stable fields are preserved. Only `status` flips and
/// `updatedAt` is refreshed. Local-only: never sent to a server.
struct RestoreProductItemUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute(_ item: ProductItem) async throws -> ProductItem {
        let restored = ProductItem(
            id: item.id,
            categoryId: item.categoryId,
            name: item.name,
            type: item.type,
            installedOrPurchasedAt: item.installedOrPurchasedAt,
            locationLabel: item.locationLabel,
            status: .active,
            careGuideIds: item.careGuideIds,
            notes: item.notes,
            imageUrl: item.imageUrl,
            updatedAt: Date()
        )
        try await repository.updateProductItem(restored)
        return restored
    }
}
