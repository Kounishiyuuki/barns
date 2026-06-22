import Foundation

/// Updates an existing local product item from edited customer-owned fields.
/// Local-only: the item is never sent to a server. Official content links
/// (categoryId, careGuideIds, imageUrl) and creation metadata are preserved;
/// only customer-editable fields change.
struct UpdateProductItemUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute(
        original: ProductItem,
        name: String,
        type: ProductItemType,
        locationLabel: String?,
        notes: String?,
        status: ProductItemStatus
    ) async throws -> ProductItem {
        let updated = ProductItem(
            id: original.id,
            categoryId: original.categoryId,
            name: name,
            type: type,
            installedOrPurchasedAt: original.installedOrPurchasedAt,
            locationLabel: locationLabel,
            status: status,
            careGuideIds: original.careGuideIds,
            notes: notes,
            imageUrl: original.imageUrl,
            updatedAt: Date()
        )
        try await repository.updateProductItem(updated)
        return updated
    }
}
