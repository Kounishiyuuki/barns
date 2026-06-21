package com.barns.app.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.barns.app.app.DependencyContainer
import com.barns.app.presentation.auth.AuthScreen
import com.barns.app.presentation.auth.AuthViewModel
import com.barns.app.presentation.care.CareScreen
import com.barns.app.presentation.catalog.CatalogScreen
import com.barns.app.presentation.home.HomeScreen
import com.barns.app.presentation.myitems.MyItemsScreen
import com.barns.app.presentation.patterns.PatternListScreen
import com.barns.app.presentation.settings.SettingsScreen
import com.barns.app.presentation.support.SupportScreen

/**
 * Composition root. Gates the source skeleton behind a mock guest sign-in,
 * then routes between the home destinations with minimal manual navigation.
 */
@Composable
fun RootScreen(
    authViewModel: AuthViewModel,
    container: DependencyContainer,
) {
    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authViewModel) {
        authViewModel.loadCurrentUser()
    }

    if (authState is AuthViewModel.State.Authenticated) {
        var route by remember { mutableStateOf<RootRoute>(RootRoute.Home) }
        when (route) {
            RootRoute.MyItems -> MyItemsScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Care -> CareScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Patterns -> PatternListScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Catalog -> CatalogScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Support -> SupportScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Settings -> SettingsScreen(container = container, onBack = { route = RootRoute.Home })
            RootRoute.Home -> {
                val homeViewModel = remember(container) { container.makeHomeViewModel() }
                HomeScreen(
                    viewModel = homeViewModel,
                    onOpenMyItems = { route = RootRoute.MyItems },
                    onOpenCare = { route = RootRoute.Care },
                    onOpenPatterns = { route = RootRoute.Patterns },
                    onOpenCatalog = { route = RootRoute.Catalog },
                    onOpenSupport = { route = RootRoute.Support },
                    onOpenSettings = { route = RootRoute.Settings },
                )
            }
        }
    } else {
        AuthScreen(viewModel = authViewModel)
    }
}

private sealed interface RootRoute {
    data object Home : RootRoute
    data object MyItems : RootRoute
    data object Care : RootRoute
    data object Patterns : RootRoute
    data object Catalog : RootRoute
    data object Support : RootRoute
    data object Settings : RootRoute
}
