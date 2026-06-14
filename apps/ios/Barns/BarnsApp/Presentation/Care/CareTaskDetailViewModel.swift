import Combine

@MainActor
final class CareTaskDetailViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(CareTask)
        case notFound
        case failed(String)
    }

    @Published private(set) var state: State = .loading
    @Published private(set) var isCompleting = false

    private let taskId: CareTask.ID
    private let getCareTaskDetailUseCase: GetCareTaskDetailUseCase
    private let completeCareTaskUseCase: CompleteCareTaskUseCase

    init(
        taskId: CareTask.ID,
        getCareTaskDetailUseCase: GetCareTaskDetailUseCase,
        completeCareTaskUseCase: CompleteCareTaskUseCase
    ) {
        self.taskId = taskId
        self.getCareTaskDetailUseCase = getCareTaskDetailUseCase
        self.completeCareTaskUseCase = completeCareTaskUseCase
    }

    func load() async {
        state = .loading
        do {
            if let task = try await getCareTaskDetailUseCase.execute(id: taskId) {
                state = .loaded(task)
            } else {
                state = .notFound
            }
        } catch {
            state = .failed("Unable to load this task. Please try again.")
        }
    }

    func complete() async {
        guard case .loaded(let task) = state, task.status != .completed, !isCompleting else { return }
        isCompleting = true
        defer { isCompleting = false }
        do {
            try await completeCareTaskUseCase.execute(id: task.id)
            await load()
        } catch {
            state = .failed("Unable to complete this task. Please try again.")
        }
    }
}
