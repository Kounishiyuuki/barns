package com.barns.app.app

import com.barns.app.data.repository.MockAuthRepository
import com.barns.app.data.repository.MockHomeRepository
import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.repository.AuthRepository
import com.barns.app.domain.repository.HomeRepository
import com.barns.app.domain.repository.ProductItemRepository
import com.barns.app.domain.usecase.auth.GetCurrentUserUseCase
import com.barns.app.domain.usecase.auth.LoginAsGuestUseCase
import com.barns.app.domain.usecase.home.GetHomeSummaryUseCase
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemDetailUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import com.barns.app.presentation.auth.AuthViewModel
import com.barns.app.presentation.home.HomeViewModel
import com.barns.app.presentation.myitems.AddItemViewModel
import com.barns.app.presentation.myitems.ItemDetailViewModel
import com.barns.app.presentation.myitems.MyItemsViewModel

/**
 * Minimal manual dependency container for the source skeleton. Mirrors the
 * iOS DependencyContainer. Repository wiring and use cases are added as
 * features are implemented; a DI framework may later live in the di package.
 */
class DependencyContainer(
    private val authRepository: AuthRepository = MockAuthRepository(),
    private val homeRepository: HomeRepository = MockHomeRepository(),
    private val productItemRepository: ProductItemRepository = MockProductItemRepository(),
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
        )

    fun makeAddItemViewModel(): AddItemViewModel =
        AddItemViewModel(
            addProductItemUseCase = AddProductItemUseCase(productItemRepository),
        )
}
