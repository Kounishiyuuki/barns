protocol ProductItemRepository {
    func productItems() async throws -> [ProductItem]
    func productItem(id: ProductItem.ID) async throws -> ProductItem?
}
