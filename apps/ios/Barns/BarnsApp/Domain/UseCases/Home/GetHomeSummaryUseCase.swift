/// Provides the lightweight home summary.
struct GetHomeSummaryUseCase {
    private let homeRepository: HomeRepository

    init(homeRepository: HomeRepository) {
        self.homeRepository = homeRepository
    }

    func execute() async throws -> HomeSummary {
        try await homeRepository.homeSummary()
    }
}
