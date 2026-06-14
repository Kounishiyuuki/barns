/// Returns the user's local care tasks.
struct GetCareTasksUseCase {
    private let repository: CareRepository

    init(repository: CareRepository) {
        self.repository = repository
    }

    func execute() async throws -> [CareTask] {
        try await repository.careTasks()
    }
}
