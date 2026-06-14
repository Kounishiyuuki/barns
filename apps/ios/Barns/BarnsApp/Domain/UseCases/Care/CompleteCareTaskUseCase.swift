/// Marks a care task as completed in local state.
struct CompleteCareTaskUseCase {
    private let repository: CareRepository

    init(repository: CareRepository) {
        self.repository = repository
    }

    func execute(id: CareTask.ID) async throws {
        try await repository.completeCareTask(id: id)
    }
}
