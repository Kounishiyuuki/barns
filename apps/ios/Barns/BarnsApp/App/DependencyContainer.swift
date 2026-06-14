struct DependencyContainer {
    private let authRepository: AuthRepository
    private let homeRepository: HomeRepository
    private let productItemRepository: ProductItemRepository

    init(
        authRepository: AuthRepository = MockAuthRepository(),
        homeRepository: HomeRepository = MockHomeRepository(),
        productItemRepository: ProductItemRepository = MockProductItemRepository()
    ) {
        self.authRepository = authRepository
        self.homeRepository = homeRepository
        self.productItemRepository = productItemRepository
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

    @MainActor
    func makeMyItemsViewModel() -> MyItemsViewModel {
        MyItemsViewModel(
            getProductItemsUseCase: GetProductItemsUseCase(repository: productItemRepository)
        )
    }

    @MainActor
    func makeItemDetailViewModel(itemId: ProductItem.ID) -> ItemDetailViewModel {
        ItemDetailViewModel(
            itemId: itemId,
            getProductItemDetailUseCase: GetProductItemDetailUseCase(repository: productItemRepository)
        )
    }

    @MainActor
    func makeAddItemViewModel() -> AddItemViewModel {
        AddItemViewModel(
            addProductItemUseCase: AddProductItemUseCase(repository: productItemRepository)
        )
    }
}
