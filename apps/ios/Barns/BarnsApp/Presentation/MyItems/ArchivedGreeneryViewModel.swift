import Combine

/// Drives the local-only Archived Greenery list. It loads the customer's
/// archived `ProductItem`s (read-only here, except restore) and can restore an
/// item back to the active My Greenery list. All work is local: no hard delete,
/// no API, no sync.
@MainActor
final class ArchivedGreeneryViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded([ProductItem])
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getProductItemsUseCase: GetProductItemsUseCase
    private let restoreProductItemUseCase: RestoreProductItemUseCase

    init(
        getProductItemsUseCase: GetProductItemsUseCase,
        restoreProductItemUseCase: RestoreProductItemUseCase
    ) {
        self.getProductItemsUseCase = getProductItemsUseCase
        self.restoreProductItemUseCase = restoreProductItemUseCase
    }

    func load() async {
        state = .loading
        do {
            let items = try await getProductItemsUseCase.execute()
            // This list shows only archived items; they remain in the local
            // store (no hard delete).
            state = .loaded(items.filter { $0.status == .archived })
        } catch {
            state = .failed("Unable to load archived items. Please try again.")
        }
    }

    /// Restores an archived item back to active locally (soft action; no hard
    /// delete). Reloads so the restored item leaves this archived list.
    /// Returns true on success.
    @discardableResult
    func restore(_ item: ProductItem) async -> Bool {
        do {
            _ = try await restoreProductItemUseCase.execute(item)
            await load()
            return true
        } catch {
            state = .failed("Unable to restore this item. Please try again.")
            return false
        }
    }
}
