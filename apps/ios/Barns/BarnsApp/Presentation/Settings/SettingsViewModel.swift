import Combine
import Foundation

/// Presentation-only view model for the Settings screen.
///
/// Settings is intentionally static and local-only for the MVP: it has no
/// repository, use case, or data-source access. It simply exposes the fixed
/// informational sections shown to the customer.
@MainActor
final class SettingsViewModel: ObservableObject {
    struct Item: Identifiable, Equatable {
        let id = UUID()
        let title: String
        let detail: String
    }

    struct Section: Identifiable, Equatable {
        let id = UUID()
        let title: String
        let items: [Item]
    }

    let sections: [Section]

    init() {
        sections = [
            Section(
                title: "App",
                items: [
                    Item(title: "Name", detail: "Barns MVP"),
                    Item(title: "Mode", detail: "Local-first / mock-first")
                ]
            ),
            Section(
                title: "About",
                items: [
                    Item(
                        title: "What barns is",
                        detail: "An after-support app for the greenery you own or had installed."
                    ),
                    Item(
                        title: "My Greenery",
                        detail: "Your own registry. Catalog and care guides are official read-only reference."
                    )
                ]
            ),
            Section(
                title: "Privacy",
                items: [
                    Item(
                        title: "Your data",
                        detail: "My Greenery stays local on this device. Nothing is sent anywhere in this MVP."
                    ),
                    Item(
                        title: "Consultation drafts",
                        detail: "Prepared locally for your reference and never submitted."
                    ),
                    Item(
                        title: "Archived greenery",
                        detail: "Archive and Restore only change status. Nothing is permanently deleted."
                    )
                ]
            ),
            Section(
                title: "Support",
                items: [
                    Item(
                        title: "Getting help",
                        detail: "Use the Support screen for phone consultation guidance. Your notes stay on this device."
                    )
                ]
            ),
            Section(
                title: "Legal",
                items: [
                    Item(
                        title: "Notices",
                        detail: "Legal and notice content is being prepared. This MVP build is not a final release."
                    )
                ]
            ),
            Section(
                title: "Development",
                items: [
                    Item(
                        title: "Status",
                        detail: "Real API, authentication, sync, and cloud storage are not enabled yet."
                    ),
                    Item(
                        title: "Not included",
                        detail: "No analytics, tracking, payments, or orders."
                    )
                ]
            )
        ]
    }
}
