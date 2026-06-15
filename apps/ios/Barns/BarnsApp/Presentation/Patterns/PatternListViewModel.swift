import Combine

@MainActor
final class PatternListViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded([WallGreenPattern])
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getPatternsUseCase: GetPatternsUseCase

    init(getPatternsUseCase: GetPatternsUseCase) {
        self.getPatternsUseCase = getPatternsUseCase
    }

    func load() async {
        state = .loading
        do {
            let patterns = try await getPatternsUseCase.execute()
            state = .loaded(patterns)
        } catch {
            state = .failed("Unable to load patterns. Please try again.")
        }
    }
}
