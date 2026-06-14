/// Returns a single registered product item by id, if present.
struct GetProductItemDetailUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute(id: ProductItem.ID) async throws -> ProductItem? {
        try await repository.productItem(id: id)
    }
}
