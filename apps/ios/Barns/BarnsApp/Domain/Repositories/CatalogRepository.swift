/// Official, read-only catalog content. Mock now; a real read-only API may
/// back this later without changing use cases or screens.
protocol CatalogRepository {
    func catalogItems() async throws -> [CatalogItem]
    func catalogItem(id: CatalogItem.ID) async throws -> CatalogItem?
}
