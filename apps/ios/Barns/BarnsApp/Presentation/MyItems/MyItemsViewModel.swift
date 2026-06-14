import Combine

@MainActor
final class MyItemsViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded([ProductItem])
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getProductItemsUseCase: GetProductItemsUseCase

    init(getProductItemsUseCase: GetProductItemsUseCase) {
        self.getProductItemsUseCase = getProductItemsUseCase
    }

    func load() async {
        state = .loading
        do {
            let items = try await getProductItemsUseCase.execute()
            state = .loaded(items)
        } catch {
            state = .failed("Unable to load items. Please try again.")
        }
    }
}
