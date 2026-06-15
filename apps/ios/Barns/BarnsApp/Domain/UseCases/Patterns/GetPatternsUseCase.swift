/// Returns the local wall-greenery patterns.
struct GetPatternsUseCase {
    private let repository: PatternRepository

    init(repository: PatternRepository) {
        self.repository = repository
    }

    func execute() async throws -> [WallGreenPattern] {
        try await repository.wallGreenPatterns()
    }
}
