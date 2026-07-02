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
                title: "App Status",
                items: [
                    Item(title: "Build", detail: "Barns MVP · Local-first / mock-first"),
                    Item(title: "Account", detail: "No account, and no cloud sync."),
                    Item(
                        title: "Images",
                        detail: "Catalog and My Greenery show local mock/demo assets, not real photos."
                    )
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
                title: "Data & Privacy",
                items: [
                    Item(
                        title: "On this device",
                        detail: "My Greenery, Care Logs, and Consultation Drafts stay on this device."
                    ),
                    Item(
                        title: "Consultation drafts",
                        detail: "Prepared locally and never submitted."
                    ),
                    Item(
                        title: "No tracking",
                        detail: "No analytics or tracking, and no advertising identifiers."
                    ),
                    Item(
                        title: "No uploads",
                        detail: "No image upload and no cloud sync."
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
                title: "Release Readiness",
                items: [
                    Item(title: "Current", detail: "Internal demo-ready."),
                    Item(
                        title: "Before release",
                        detail: "Store / TestFlight release still needs manual QA, privacy and legal review, and release configuration."
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
            )
        ]
    }
}
