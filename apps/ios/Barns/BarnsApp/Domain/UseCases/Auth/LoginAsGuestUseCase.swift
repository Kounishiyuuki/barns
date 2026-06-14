/// Signs in with the mock guest account.
struct LoginAsGuestUseCase {
    private let authRepository: AuthRepository

    init(authRepository: AuthRepository) {
        self.authRepository = authRepository
    }

    func execute() async throws -> User {
        try await authRepository.signInWithMockAccount()
    }
}
