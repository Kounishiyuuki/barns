import Foundation

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var state: HomeUiState

    init(state: HomeUiState = .placeholder) {
        self.state = state
    }
}

struct HomeUiState: Equatable {
    let title: String
    let message: String

    static let placeholder = HomeUiState(
        title: "After-sales care starts here",
        message: "Your registered greenery, care reminders, and phone consultation guidance will appear here."
    )
}
