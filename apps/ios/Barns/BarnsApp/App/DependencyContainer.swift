struct DependencyContainer {
    private let authRepository: AuthRepository
    private let homeRepository: HomeRepository
    private let productItemRepository: ProductItemRepository
    private let careRepository: CareRepository
    private let patternRepository: PatternRepository
    private let supportRepository: SupportRepository
    private let consultationDraftRepository: ConsultationDraftRepository
    // Official read-only content (mock now; replaceable by a read-only API).
    private let catalogRepository: CatalogRepository
    private let greeneryInfoRepository: GreeneryInfoRepository
    private let careGuideRepository: CareGuideRepository

    init(
        authRepository: AuthRepository = MockAuthRepository(),
        homeRepository: HomeRepository = MockHomeRepository(),
        productItemRepository: ProductItemRepository = MockProductItemRepository(),
        careRepository: CareRepository = MockCareRepository(),
        patternRepository: PatternRepository = MockPatternRepository(),
        supportRepository: SupportRepository = MockSupportRepository(),
        consultationDraftRepository: ConsultationDraftRepository = MockConsultationDraftRepository(),
        catalogRepository: CatalogRepository = MockCatalogRepository(),
        greeneryInfoRepository: GreeneryInfoRepository = MockGreeneryInfoRepository(),
        careGuideRepository: CareGuideRepository = MockCareGuideRepository()
    ) {
        self.authRepository = authRepository
        self.homeRepository = homeRepository
        self.productItemRepository = productItemRepository
        self.careRepository = careRepository
        self.patternRepository = patternRepository
        self.supportRepository = supportRepository
        self.consultationDraftRepository = consultationDraftRepository
        self.catalogRepository = catalogRepository
        self.greeneryInfoRepository = greeneryInfoRepository
        self.careGuideRepository = careGuideRepository
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
            getProductItemDetailUseCase: GetProductItemDetailUseCase(repository: productItemRepository),
            getGreeneryInfoUseCase: GetGreeneryInfoUseCase(repository: greeneryInfoRepository),
            getCareGuidesUseCase: GetCareGuidesUseCase(repository: careGuideRepository)
        )
    }

    @MainActor
    func makeAddItemViewModel(prefill: RegisterGreeneryPrefill? = nil) -> AddItemViewModel {
        AddItemViewModel(
            addProductItemUseCase: AddProductItemUseCase(repository: productItemRepository),
            prefill: prefill
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
    func makeConsultationDraftViewModel(for item: ProductItem? = nil) -> ConsultationDraftViewModel {
        ConsultationDraftViewModel(
            getConsultationDraftUseCase: GetConsultationDraftUseCase(repository: consultationDraftRepository),
            saveConsultationDraftUseCase: SaveConsultationDraftUseCase(repository: consultationDraftRepository),
            item: item
        )
    }

    // MARK: - Official read-only content use cases
    // Exposed as use cases (not repositories) so future Catalog screens depend
    // only on the domain, and the mock repositories can be swapped for an API.

    func makeGetCatalogItemsUseCase() -> GetCatalogItemsUseCase {
        GetCatalogItemsUseCase(repository: catalogRepository)
    }

    func makeGetCatalogItemDetailUseCase() -> GetCatalogItemDetailUseCase {
        GetCatalogItemDetailUseCase(repository: catalogRepository)
    }

    func makeGetGreeneryInfoUseCase() -> GetGreeneryInfoUseCase {
        GetGreeneryInfoUseCase(repository: greeneryInfoRepository)
    }

    func makeGetCareGuidesUseCase() -> GetCareGuidesUseCase {
        GetCareGuidesUseCase(repository: careGuideRepository)
    }

    @MainActor
    func makeCatalogListViewModel() -> CatalogListViewModel {
        CatalogListViewModel(getCatalogItemsUseCase: makeGetCatalogItemsUseCase())
    }

    @MainActor
    func makeCatalogDetailViewModel(itemId: String) -> CatalogDetailViewModel {
        CatalogDetailViewModel(
            itemId: itemId,
            getCatalogItemDetailUseCase: makeGetCatalogItemDetailUseCase(),
            getGreeneryInfoUseCase: makeGetGreeneryInfoUseCase(),
            getCareGuidesUseCase: makeGetCareGuidesUseCase()
        )
    }
}
