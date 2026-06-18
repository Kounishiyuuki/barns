package com.barns.app.presentation.settings

import androidx.lifecycle.ViewModel

/**
 * Presentation-only view model for the Settings screen.
 *
 * Settings is intentionally static and local-only for the MVP: it has no
 * repository, use case, or data-source access. It simply exposes the fixed
 * informational sections shown to the customer. Mirrors the iOS Settings
 * screen content for cross-platform parity.
 */
class SettingsViewModel : ViewModel() {
    data class Item(
        val title: String,
        val detail: String,
    )

    data class Section(
        val title: String,
        val items: List<Item>,
    )

    val sections: List<Section> = listOf(
        Section(
            title = "App",
            items = listOf(
                Item(title = "Name", detail = "Barns MVP"),
                Item(title = "Mode", detail = "Local-first / mock-first"),
            ),
        ),
        Section(
            title = "Support",
            items = listOf(
                Item(
                    title = "Getting help",
                    detail = "Use the Support screen for phone consultation guidance.",
                ),
            ),
        ),
        Section(
            title = "Privacy",
            items = listOf(
                Item(
                    title = "Your data",
                    detail = "Customer-side data stays local on this device in the current MVP.",
                ),
            ),
        ),
        Section(
            title = "Development",
            items = listOf(
                Item(
                    title = "Status",
                    detail = "Real API, authentication, and persistence are not enabled yet.",
                ),
            ),
        ),
    )
}
