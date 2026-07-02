import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel
    private let container: DependencyContainer

    init(viewModel: HomeViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        NavigationStack {
            content
                .navigationTitle("barns")
        }
        .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ProgressView()
        case .failed(let message):
            Text(message)
                .foregroundStyle(.secondary)
        case .loaded(let content):
            List {
                Section {
                    // Calm dashboard hero: a warm greeting over a short
                    // after-support line, giving Home a clear top-level
                    // identity rather than opening straight into a menu.
                    VStack(alignment: .leading, spacing: 6) {
                        Text(content.greeting)
                            .font(.title3)
                            .fontWeight(.semibold)
                        Text(content.summary.welcomeMessage)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.vertical, 4)
                    .accessibilityElement(children: .combine)
                }
                Section("Your greenery") {
                    NavigationLink {
                        MyItemsView(viewModel: container.makeMyItemsViewModel(), container: container)
                    } label: {
                        homeRow(
                            title: "My Greenery",
                            subtitle: "\(content.summary.registeredItemCount) registered locally"
                        )
                    }
                    NavigationLink {
                        CareView(viewModel: container.makeCareViewModel(), container: container)
                    } label: {
                        homeRow(title: "Next care", subtitle: content.summary.nextCareLabel)
                    }
                }
                Section("Explore") {
                    NavigationLink {
                        PatternListView(viewModel: container.makePatternListViewModel(), container: container)
                    } label: {
                        homeRow(title: "Patterns", subtitle: content.summary.patternsEntryLabel)
                    }
                    NavigationLink {
                        CatalogListView(viewModel: container.makeCatalogListViewModel(), container: container)
                    } label: {
                        homeRow(title: "Explore official catalog", subtitle: "Official read-only reference")
                    }
                }
                Section("Support") {
                    NavigationLink {
                        SupportView(viewModel: container.makeSupportViewModel(), container: container)
                    } label: {
                        homeRow(title: "Support", subtitle: content.summary.supportGuidance)
                    }
                }
                Section("More") {
                    NavigationLink {
                        SettingsView(viewModel: container.makeSettingsViewModel())
                    } label: {
                        homeRow(title: "Settings", subtitle: "App status and guardrails")
                    }
                }
            }
        }
    }

    /// Consistent two-line entry row: a clear destination title over a short
    /// supporting line. Keeps every Home destination on the same rhythm and
    /// mirrors the Android Home list for cross-platform parity.
    private func homeRow(title: String, subtitle: String) -> some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(title)
            Text(subtitle)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .padding(.vertical, 2)
        .accessibilityElement(children: .combine)
    }
}

#Preview {
    HomeView(
        viewModel: DependencyContainer().makeHomeViewModel(),
        container: DependencyContainer()
    )
}
