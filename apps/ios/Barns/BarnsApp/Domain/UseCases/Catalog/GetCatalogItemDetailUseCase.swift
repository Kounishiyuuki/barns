/// Returns a single official catalog item by id, if it exists.
struct GetCatalogItemDetailUseCase {
    private let repository: CatalogRepository

    init(repository: CatalogRepository) {
        self.repository = repository
    }

    func execute(id: CatalogItem.ID) async throws -> CatalogItem? {
        try await repository.catalogItem(id: id)
    }
}
