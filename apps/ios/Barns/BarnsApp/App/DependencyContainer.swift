struct DependencyContainer {
    private let authRepository: AuthRepository
    private let homeRepository: HomeRepository
    private let productItemRepository: ProductItemRepository
    private let careRepository: CareRepository
    private let patternRepository: PatternRepository
    private let supportRepository: SupportRepository
    private let consultationDraftRepository: ConsultationDraftRepository

    init(
        authRepository: AuthRepository = MockAuthRepository(),
        homeRepository: HomeRepository = MockHomeRepository(),
        productItemRepository: ProductItemRepository = MockProductItemRepository(),
        careRepository: CareRepository = MockCareRepository(),
        patternRepository: PatternRepository = MockPatternRepository(),
        supportRepository: SupportRepository = MockSupportRepository(),
        consultationDraftRepository: ConsultationDraftRepository = MockConsultationDraftRepository()
    ) {
        self.authRepository = authRepository
        self.homeRepository = homeRepository
        self.productItemRepository = productItemRepository
        self.careRepository = careRepository
        self.patternRepository = patternRepository
        self.supportRepository = supportRepository
        self.consultationDraftRepository = consultationDraftRepository
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

    @MainActor
    func makeCareViewModel() -> CareViewModel {
        CareViewModel(
            getCareTasksUseCase: GetCareTasksUseCase(repository: careRepository),
            getCareLogsUseCase: GetCareLogsUseCase(repository: careRepository)
        )
    }

    @MainActor
    func makeCareTaskDetailViewModel(taskId: CareTask.ID) -> CareTaskDetailViewModel {
        CareTaskDetailViewModel(
            taskId: taskId,
            getCareTaskDetailUseCase: GetCareTaskDetailUseCase(repository: careRepository),
            completeCareTaskUseCase: CompleteCareTaskUseCase(repository: careRepository)
        )
    }

    @MainActor
    func makePatternListViewModel() -> PatternListViewModel {
        PatternListViewModel(
            getPatternsUseCase: GetPatternsUseCase(repository: patternRepository)
        )
    }

    @MainActor
    func makePatternDetailViewModel(patternId: WallGreenPattern.ID) -> PatternDetailViewModel {
        PatternDetailViewModel(
            patternId: patternId,
            getPatternDetailUseCase: GetPatternDetailUseCase(repository: patternRepository)
        )
    }

    @MainActor
    func makeSupportViewModel() -> SupportViewModel {
        SupportViewModel(
            getSupportInfoUseCase: GetSupportInfoUseCase(repository: supportRepository)
        )
    }

    @MainActor
    func makePhoneInquiryViewModel() -> PhoneInquiryViewModel {
        PhoneInquiryViewModel(
            getSupportInfoUseCase: GetSupportInfoUseCase(repository: supportRepository)
        )
    }

    @MainActor
    func makeSettingsViewModel() -> SettingsViewModel {
        SettingsViewModel()
    }

    @MainActor
    func makeConsultationDraftViewModel() -> ConsultationDraftViewModel {
        ConsultationDraftViewModel(
            getConsultationDraftUseCase: GetConsultationDraftUseCase(repository: consultationDraftRepository),
            saveConsultationDraftUseCase: SaveConsultationDraftUseCase(repository: consultationDraftRepository)
        )
    }
}
