/// Returns a single care task by id, if present.
struct GetCareTaskDetailUseCase {
    private let repository: CareRepository

    init(repository: CareRepository) {
        self.repository = repository
    }

    func execute(id: CareTask.ID) async throws -> CareTask? {
        try await repository.careTask(id: id)
    }
}
