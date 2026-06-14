import SwiftUI

/// Composition root. Gates the app behind a mock guest sign-in,
/// then shows the home flow. Intentionally minimal navigation.
struct RootView: View {
    @StateObject private var authViewModel: AuthViewModel
    private let container: DependencyContainer

    init(authViewModel: AuthViewModel, container: DependencyContainer) {
        _authViewModel = StateObject(wrappedValue: authViewModel)
        self.container = container
    }

    var body: some View {
        if authViewModel.isAuthenticated {
            HomeView(viewModel: container.makeHomeViewModel())
        } else {
            AuthView(viewModel: authViewModel)
        }
    }
}
