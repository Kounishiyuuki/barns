/// Returns the user's locally registered product items.
struct GetProductItemsUseCase {
    private let repository: ProductItemRepository

    init(repository: ProductItemRepository) {
        self.repository = repository
    }

    func execute() async throws -> [ProductItem] {
        try await repository.productItems()
    }
}
