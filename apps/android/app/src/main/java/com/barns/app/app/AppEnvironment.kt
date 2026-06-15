package com.barns.app.app

/**
 * Composition root holder. Mirrors the iOS AppEnvironment.
 */
data class AppEnvironment(
    val dependencyContainer: DependencyContainer,
) {
    companion object {
        fun makeDefault(): AppEnvironment =
            AppEnvironment(dependencyContainer = DependencyContainer())
    }
}
