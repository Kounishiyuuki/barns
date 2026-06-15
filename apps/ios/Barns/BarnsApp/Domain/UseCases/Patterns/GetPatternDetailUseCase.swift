/// Returns a single wall-greenery pattern by id, if present.
struct GetPatternDetailUseCase {
    private let repository: PatternRepository

    init(repository: PatternRepository) {
        self.repository = repository
    }

    func execute(id: WallGreenPattern.ID) async throws -> WallGreenPattern? {
        try await repository.wallGreenPattern(id: id)
    }
}
