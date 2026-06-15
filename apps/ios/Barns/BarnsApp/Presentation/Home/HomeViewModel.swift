import Combine

@MainActor
final class HomeViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(HomeContent)
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getHomeSummaryUseCase: GetHomeSummaryUseCase
    private let getCurrentUserUseCase: GetCurrentUserUseCase

    init(
        getHomeSummaryUseCase: GetHomeSummaryUseCase,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ) {
        self.getHomeSummaryUseCase = getHomeSummaryUseCase
        self.getCurrentUserUseCase = getCurrentUserUseCase
    }

    func load() async {
        state = .loading
        do {
            let summary = try await getHomeSummaryUseCase.execute()
            let user = try await getCurrentUserUseCase.execute()
            state = .loaded(HomeContent(greeting: greeting(for: user), summary: summary))
        } catch {
            state = .failed("Unable to load home. Please try again.")
        }
    }

    private func greeting(for user: User?) -> String {
        guard let user else { return "Welcome to barns" }
        return "Welcome back, \(user.displayName)"
    }
}

struct HomeContent: Equatable {
    let greeting: String
    let summary: HomeSummary
}
