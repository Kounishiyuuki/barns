/// Returns local care logs, optionally filtered by product item.
struct GetCareLogsUseCase {
    private let repository: CareRepository

    init(repository: CareRepository) {
        self.repository = repository
    }

    func execute(productItemId: ProductItem.ID? = nil) async throws -> [CareLog] {
        try await repository.careLogs(for: productItemId)
    }
}
