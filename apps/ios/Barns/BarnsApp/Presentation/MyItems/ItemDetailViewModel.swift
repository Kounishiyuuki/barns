import Combine

@MainActor
final class ItemDetailViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(ProductItem)
        case notFound
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let itemId: ProductItem.ID
    private let getProductItemDetailUseCase: GetProductItemDetailUseCase

    init(itemId: ProductItem.ID, getProductItemDetailUseCase: GetProductItemDetailUseCase) {
        self.itemId = itemId
        self.getProductItemDetailUseCase = getProductItemDetailUseCase
    }

    func load() async {
        state = .loading
        do {
            if let item = try await getProductItemDetailUseCase.execute(id: itemId) {
                state = .loaded(item)
            } else {
                state = .notFound
            }
        } catch {
            state = .failed("Unable to load this item. Please try again.")
        }
    }
}
