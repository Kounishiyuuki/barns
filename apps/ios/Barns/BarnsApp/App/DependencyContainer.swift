struct DependencyContainer {
    private let authRepository: AuthRepository
    private let homeRepository: HomeRepository

    init(
        authRepository: AuthRepository = MockAuthRepository(),
        homeRepository: HomeRepository = MockHomeRepository()
    ) {
        self.authRepository = authRepository
        self.homeRepository = homeRepository
    }

    @MainActor
    func makeAuthViewModel() -> AuthViewModel {
        AuthViewModel(
            loginAsGuestUseCase: LoginAsGuestUseCase(authRepository: authRepository),
            getCurrentUserUseCase: GetCurrentUserUseCase(authRepository: authRepository)
        )
    }

    @MainActor
    func makeHomeViewModel() -> HomeViewModel {
        HomeViewModel(
            getHomeSummaryUseCase: GetHomeSummaryUseCase(homeRepository: homeRepository),
            getCurrentUserUseCase: GetCurrentUserUseCase(authRepository: authRepository)
        )
    }
}
