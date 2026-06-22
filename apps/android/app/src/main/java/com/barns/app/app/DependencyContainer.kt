package com.barns.app.app

import com.barns.app.data.repository.MockAuthRepository
import com.barns.app.data.repository.MockCareRepository
import com.barns.app.data.repository.MockConsultationDraftRepository
import com.barns.app.data.repository.MockHomeRepository
import com.barns.app.data.repository.MockPatternRepository
import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.data.repository.MockSupportRepository
import com.barns.app.domain.model.ProductItem
import com.barns.app.data.repository.MockCareGuideRepository
import com.barns.app.data.repository.MockCatalogRepository
import com.barns.app.data.repository.MockGreeneryInfoRepository
import com.barns.app.domain.repository.AuthRepository
import com.barns.app.domain.repository.CareGuideRepository
import com.barns.app.domain.repository.CatalogRepository
import com.barns.app.domain.repository.GreeneryInfoRepository
import com.barns.app.domain.usecase.catalog.GetCareGuidesUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemDetailUseCase
import com.barns.app.domain.usecase.catalog.GetCatalogItemsUseCase
import com.barns.app.domain.usecase.catalog.GetGreeneryInfoUseCase
import com.barns.app.domain.repository.CareRepository
import com.barns.app.domain.repository.ConsultationDraftRepository
import com.barns.app.domain.repository.HomeRepository
import com.barns.app.domain.repository.PatternRepository
import com.barns.app.domain.repository.ProductItemRepository
import com.barns.app.domain.repository.SupportRepository
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.auth.LoginAsGuestUseCase
import com.barns.app.domain.usecase.care.CompleteCareTaskUseCase
import com.barns.app.domain.usecase.care.GetCareLogsUseCase
import com.barns.app.domain.usecase.care.GetCareTaskDetailUseCase
import com.barns.app.domain.usecase.care.GetCareTasksUseCase
import com.barns.app.domain.usecase.home.GetHomeSummaryUseCase
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemDetailUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.domain.usecase.patterns.GetPatternDetailUseCase
import com.barns.app.domain.usecase.patterns.GetPatternsUseCase
import com.barns.app.domain.usecase.support.GetConsultationDraftUseCase
import com.barns.app.domain.usecase.support.GetSupportInfoUseCase
import com.barns.app.domain.usecase.support.SaveConsultationDraftUseCase
import com.barns.app.presentation.auth.AuthViewModel
import com.barns.app.presentation.catalog.CatalogDetailViewModel
import com.barns.app.presentation.catalog.CatalogListViewModel
import com.barns.app.presentation.care.CareTaskDetailViewModel
import com.barns.app.presentation.care.CareViewModel
import com.barns.app.presentation.home.HomeViewModel
import com.barns.app.presentation.myitems.AddItemViewModel
import com.barns.app.presentation.myitems.RegisterGreeneryPrefill
import com.barns.app.presentation.myitems.ItemDetailViewModel
import com.barns.app.presentation.myitems.ItemOfficialContentResolver
import com.barns.app.presentation.myitems.MyItemsViewModel
import com.barns.app.presentation.patterns.PatternDetailViewModel
import com.barns.app.presentation.patterns.PatternListViewModel
import com.barns.app.presentation.settings.SettingsViewModel
import com.barns.app.presentation.support.ConsultationDraftViewModel
import com.barns.app.presentation.support.PhoneInquiryViewModel
import com.barns.app.presentation.support.SupportViewModel

/**
 * Minimal manual dependency container for the source skeleton. Mirrors the
 * iOS DependencyContainer. Repository wiring and use cases are added as
 * features are implemented; a DI framework may later live in the di package.
 */
class DependencyContainer(
    private val authRepository: AuthRepository = MockAuthRepository(),
    private val homeRepository: HomeRepository = MockHomeRepository(),
    private val productItemRepository: ProductItemRepository = MockProductItemRepository(),
    private val careRepository: CareRepository = MockCareRepository(),
    private val patternRepository: PatternRepository = MockPatternRepository(),
    private val supportRepository: SupportRepository = MockSupportRepository(),
    private val consultationDraftRepository: ConsultationDraftRepository = MockConsultationDraftRepository(),
    // Official read-only content (mock now; replaceable by a read-only API).
    private val catalogRepository: CatalogRepository = MockCatalogRepository(),
    private val greeneryInfoRepository: GreeneryInfoRepository = MockGreeneryInfoRepository(),
    private val careGuideRepository: CareGuideRepository = MockCareGuideRepository(),
) {
    fun makeAuthViewModel(): AuthViewModel =
        AuthViewModel(
            loginAsGuestUseCase = LoginAsGuestUseCase(authRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        )

    fun makeHomeViewModel(): HomeViewModel =
        HomeViewModel(
            getHomeSummaryUseCase = GetHomeSummaryUseCase(homeRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
        )

    fun makeMyItemsViewModel(): MyItemsViewModel =
        MyItemsViewModel(
            getProductItemsUseCase = GetProductItemsUseCase(productItemRepository),
        )

    fun makeItemDetailViewModel(itemId: String): ItemDetailViewModel =
        ItemDetailViewModel(
            itemId = itemId,
            getProductItemDetailUseCase = GetProductItemDetailUseCase(productItemRepository),
            officialContentResolver = ItemOfficialContentResolver(
                getGreeneryInfoUseCase = GetGreeneryInfoUseCase(greeneryInfoRepository),
                getCareGuidesUseCase = GetCareGuidesUseCase(careGuideRepository),
            ),
        )

    fun makeAddItemViewModel(prefill: RegisterGreeneryPrefill? = null): AddItemViewModel =
        AddItemViewModel(
            addProductItemUseCase = AddProductItemUseCase(productItemRepository),
            prefill = prefill,
        )

    fun makeCareViewModel(): CareViewModel =
        CareViewModel(
            getCareTasksUseCase = GetCareTasksUseCase(careRepository),
            getCareLogsUseCase = GetCareLogsUseCase(careRepository),
        )

    fun makeCareTaskDetailViewModel(taskId: String): CareTaskDetailViewModel =
        CareTaskDetailViewModel(
            taskId = taskId,
            getCareTaskDetailUseCase = GetCareTaskDetailUseCase(careRepository),
            completeCareTaskUseCase = CompleteCareTaskUseCase(careRepository),
        )

    fun makePatternListViewModel(): PatternListViewModel =
        PatternListViewModel(
            getPatternsUseCase = GetPatternsUseCase(patternRepository),
        )

    fun makePatternDetailViewModel(patternId: String): PatternDetailViewModel =
        PatternDetailViewModel(
            patternId = patternId,
            getPatternDetailUseCase = GetPatternDetailUseCase(patternRepository),
        )

    fun makeSupportViewModel(): SupportViewModel =
        SupportViewModel(
            getSupportInfoUseCase = GetSupportInfoUseCase(supportRepository),
        )

    fun makePhoneInquiryViewModel(): PhoneInquiryViewModel =
        PhoneInquiryViewModel(
            getSupportInfoUseCase = GetSupportInfoUseCase(supportRepository),
        )

    fun makeSettingsViewModel(): SettingsViewModel = SettingsViewModel()

    fun makeConsultationDraftViewModel(item: ProductItem? = null): ConsultationDraftViewModel =
        ConsultationDraftViewModel(
            getConsultationDraftUseCase = GetConsultationDraftUseCase(consultationDraftRepository),
            saveConsultationDraftUseCase = SaveConsultationDraftUseCase(consultationDraftRepository),
            item = item,
        )

    // Official read-only content use cases. Exposed as use cases (not
    // repositories) so future Catalog screens depend only on the domain, and
    // the mock repositories can be swapped for an API.

    fun makeGetCatalogItemsUseCase(): GetCatalogItemsUseCase =
        GetCatalogItemsUseCase(catalogRepository)

    fun makeGetCatalogItemDetailUseCase(): GetCatalogItemDetailUseCase =
        GetCatalogItemDetailUseCase(catalogRepository)

    fun makeGetGreeneryInfoUseCase(): GetGreeneryInfoUseCase =
        GetGreeneryInfoUseCase(greeneryInfoRepository)

    fun makeGetCareGuidesUseCase(): GetCareGuidesUseCase =
        GetCareGuidesUseCase(careGuideRepository)

    fun makeCatalogListViewModel(): CatalogListViewModel =
        CatalogListViewModel(getCatalogItemsUseCase = makeGetCatalogItemsUseCase())

    fun makeCatalogDetailViewModel(itemId: String): CatalogDetailViewModel =
        CatalogDetailViewModel(
            itemId = itemId,
            getCatalogItemDetailUseCase = makeGetCatalogItemDetailUseCase(),
            getGreeneryInfoUseCase = makeGetGreeneryInfoUseCase(),
            getCareGuidesUseCase = makeGetCareGuidesUseCase(),
        )
}
