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
            title = "About",
            items = listOf(
                Item(
                    title = "What barns is",
                    detail = "An after-support app for the greenery you own or had installed.",
                ),
                Item(
                    title = "My Greenery",
                    detail = "Your own registry. Catalog and care guides are official read-only reference.",
                ),
            ),
        ),
        Section(
            title = "Privacy",
            items = listOf(
                Item(
                    title = "Your data",
                    detail = "My Greenery stays local on this device. Nothing is sent anywhere in this MVP.",
                ),
                Item(
                    title = "Consultation drafts",
                    detail = "Prepared locally for your reference and never submitted.",
                ),
                Item(
                    title = "Archived greenery",
                    detail = "Archive and Restore only change status. Nothing is permanently deleted.",
                ),
            ),
        ),
        Section(
            title = "Support",
            items = listOf(
                Item(
                    title = "Getting help",
                    detail = "Use the Support screen for phone consultation guidance. Your notes stay on this device.",
                ),
            ),
        ),
        Section(
            title = "Legal",
            items = listOf(
                Item(
                    title = "Notices",
                    detail = "Legal and notice content is being prepared. This MVP build is not a final release.",
                ),
            ),
        ),
        Section(
            title = "Development",
            items = listOf(
                Item(
                    title = "Status",
                    detail = "Real API, authentication, sync, and cloud storage are not enabled yet.",
                ),
                Item(
                    title = "Not included",
                    detail = "No analytics, tracking, payments, or orders.",
                ),
            ),
        ),
    )
}
