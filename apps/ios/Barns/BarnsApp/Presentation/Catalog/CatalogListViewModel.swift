import Combine

/// Loads the supporting, official read-only catalog list through a use case.
@MainActor
final class CatalogListViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded([CatalogPresentationItem])
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getCatalogItemsUseCase: GetCatalogItemsUseCase

    init(getCatalogItemsUseCase: GetCatalogItemsUseCase) {
        self.getCatalogItemsUseCase = getCatalogItemsUseCase
    }

    func load() async {
        state = .loading
        do {
            let items = try await getCatalogItemsUseCase.execute()
            state = .loaded(items.map(CatalogPresentationItem.init))
        } catch {
            state = .failed("Unable to load the catalog. Please try again.")
        }
    }
}
