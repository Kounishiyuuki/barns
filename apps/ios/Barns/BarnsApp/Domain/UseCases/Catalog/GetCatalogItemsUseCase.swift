/// Returns the official, read-only catalog items.
struct GetCatalogItemsUseCase {
    private let repository: CatalogRepository

    init(repository: CatalogRepository) {
        self.repository = repository
    }

    func execute() async throws -> [CatalogItem] {
        try await repository.catalogItems()
    }
}
