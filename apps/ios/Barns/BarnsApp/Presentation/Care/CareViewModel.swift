import Combine

@MainActor
final class CareViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(CareContent)
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getCareTasksUseCase: GetCareTasksUseCase
    private let getCareLogsUseCase: GetCareLogsUseCase

    init(
        getCareTasksUseCase: GetCareTasksUseCase,
        getCareLogsUseCase: GetCareLogsUseCase
    ) {
        self.getCareTasksUseCase = getCareTasksUseCase
        self.getCareLogsUseCase = getCareLogsUseCase
    }

    func load() async {
        state = .loading
        do {
            let tasks = try await getCareTasksUseCase.execute()
            let logs = try await getCareLogsUseCase.execute()
            let upcoming = tasks.filter { $0.status == .pending }
            state = .loaded(CareContent(upcoming: upcoming, recentLogs: logs))
        } catch {
            state = .failed("Unable to load care. Please try again.")
        }
    }
}

struct CareContent: Equatable {
    let upcoming: [CareTask]
    let recentLogs: [CareLog]
}
