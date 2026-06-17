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
                title: "Support",
                items: [
                    Item(
                        title: "Getting help",
                        detail: "Use the Support screen for phone consultation guidance."
                    )
                ]
            ),
            Section(
                title: "Privacy",
                items: [
                    Item(
                        title: "Your data",
                        detail: "Customer-side data stays local on this device in the current MVP."
                    )
                ]
            ),
            Section(
                title: "Development",
                items: [
                    Item(
                        title: "Status",
                        detail: "Real API, authentication, and persistence are not enabled yet."
                    )
                ]
            )
        ]
    }
}
