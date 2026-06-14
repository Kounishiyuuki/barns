import Combine

@MainActor
final class AuthViewModel: ObservableObject {
    enum State: Equatable {
        case idle
        case loading
        case authenticated(User)
        case failed(String)
    }

    @Published private(set) var state: State = .idle

    private let loginAsGuestUseCase: LoginAsGuestUseCase
    private let getCurrentUserUseCase: GetCurrentUserUseCase

    init(
        loginAsGuestUseCase: LoginAsGuestUseCase,
        getCurrentUserUseCase: GetCurrentUserUseCase
    ) {
        self.loginAsGuestUseCase = loginAsGuestUseCase
        self.getCurrentUserUseCase = getCurrentUserUseCase
    }

    var isAuthenticated: Bool {
        if case .authenticated = state { return true }
        return false
    }

    func loginAsGuest() async {
        state = .loading
        do {
            let user = try await loginAsGuestUseCase.execute()
            state = .authenticated(user)
        } catch {
            state = .failed("Sign-in failed. Please try again.")
        }
    }
}
