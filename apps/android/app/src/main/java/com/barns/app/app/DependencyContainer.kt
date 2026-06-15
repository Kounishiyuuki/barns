package com.barns.app.app

import com.barns.app.presentation.home.HomeViewModel

/**
 * Minimal manual dependency container for the source skeleton. Mirrors the
 * iOS DependencyContainer. Repository wiring and use cases are added as
 * features are implemented; a DI framework may later live in the di package.
 */
class DependencyContainer {
    fun makeHomeViewModel(): HomeViewModel = HomeViewModel()
}
