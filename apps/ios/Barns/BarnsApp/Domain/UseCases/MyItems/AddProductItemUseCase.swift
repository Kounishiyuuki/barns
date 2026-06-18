import Foundation

/// Adds a new local product item built from user input.
/// Local-only: the item is never sent to a server.
struct AddProductItemUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute(
        name: String,
        categoryId: String,
        type: ProductItemType = .purchased,
        locationLabel: String?,
        notes: String?
    ) async throws -> ProductItem {
        let item = ProductItem(
            id: UUID().uuidString,
            categoryId: categoryId,
            name: name,
            type: type,
            installedOrPurchasedAt: nil,
            locationLabel: locationLabel,
            status: .active,
            careGuideIds: [],
            notes: notes,
            imageUrl: nil,
            updatedAt: Date()
        )
        try await repository.addProductItem(item)
        return item
    }
}
