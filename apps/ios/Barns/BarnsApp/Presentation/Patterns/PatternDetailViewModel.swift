import Combine

@MainActor
final class PatternDetailViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(WallGreenPattern)
        case notFound
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let patternId: WallGreenPattern.ID
    private let getPatternDetailUseCase: GetPatternDetailUseCase

    init(patternId: WallGreenPattern.ID, getPatternDetailUseCase: GetPatternDetailUseCase) {
        self.patternId = patternId
        self.getPatternDetailUseCase = getPatternDetailUseCase
    }

    func load() async {
        state = .loading
        do {
            if let pattern = try await getPatternDetailUseCase.execute(id: patternId) {
                state = .loaded(pattern)
            } else {
                state = .notFound
            }
        } catch {
            state = .failed("Unable to load this pattern. Please try again.")
        }
    }
}
