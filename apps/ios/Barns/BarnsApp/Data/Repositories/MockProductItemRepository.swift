import Foundation

/// In-memory, local-only product items. No persistence, no network.
/// Seed data is fake, non-sensitive, and image-null compatible.
actor MockProductItemRepository: ProductItemRepository {
    private var items: [ProductItem]

    init() {
        items = [
            ProductItem(
                id: "item-wall-green-001",
                categoryId: "cat-wall-green",
                name: "Entryway green wall",
                type: .installed,
                installedOrPurchasedAt: nil,
                locationLabel: "Entryway",
                status: .active,
                careGuideIds: [],
                notes: "Installed green wall in the main entrance.",
                imageUrl: URL(string: "mock://my-greenery/my-greenery-entryway-green-wall-01"),
                updatedAt: nil
            ),
            ProductItem(
                id: "item-interior-green-001",
                categoryId: "cat-interior-green",
                name: "Reception foliage planter",
                type: .purchased,
                installedOrPurchasedAt: nil,
                locationLabel: "Reception counter",
                status: .active,
                careGuideIds: [],
                notes: "Potted foliage on the reception counter.",
                imageUrl: URL(string: "mock://my-greenery/my-greenery-reception-foliage-planter-01"),
                updatedAt: nil
            )
        ]
    }

    func productItems() async throws -> [ProductItem] {
        items
    }

    func productItem(id: ProductItem.ID) async throws -> ProductItem? {
        items.first { $0.id == id }
    }

    func addProductItem(_ item: ProductItem) async throws {
        items.append(item)
    }

    func updateProductItem(_ item: ProductItem) async throws {
        guard let index = items.firstIndex(where: { $0.id == item.id }) else { return }
        items[index] = item
    }
}
