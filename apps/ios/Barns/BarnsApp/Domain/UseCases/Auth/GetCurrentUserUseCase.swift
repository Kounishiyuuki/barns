/// Returns the currently signed-in user, if any.
struct GetCurrentUserUseCase {
    private let authRepository: AuthRepository

    init(authRepository: AuthRepository) {
        self.authRepository = authRepository
    }

    func execute() async throws -> User? {
        try await authRepository.currentUser()
    }
}
