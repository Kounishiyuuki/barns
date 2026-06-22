protocol ProductItemRepository {
    func productItems() async throws -> [ProductItem]
    func productItem(id: ProductItem.ID) async throws -> ProductItem?
    func addProductItem(_ item: ProductItem) async throws
    /// Replaces the locally stored item with the same id. Local-only; never
    /// sent to a server.
    func updateProductItem(_ item: ProductItem) async throws
}
