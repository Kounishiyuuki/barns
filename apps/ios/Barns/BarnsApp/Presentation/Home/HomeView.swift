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
                    Text(content.greeting)
                        .font(.headline)
                    Text(content.summary.welcomeMessage)
                        .foregroundStyle(.secondary)
                }
                Section("Your greenery") {
                    NavigationLink {
                        MyItemsView(viewModel: container.makeMyItemsViewModel(), container: container)
                    } label: {
                        LabeledContent("My Greenery", value: "\(content.summary.registeredItemCount)")
                    }
                    NavigationLink {
                        CareView(viewModel: container.makeCareViewModel(), container: container)
                    } label: {
                        LabeledContent("Next care", value: content.summary.nextCareLabel)
                    }
                }
                Section("Explore") {
                    NavigationLink {
                        PatternListView(viewModel: container.makePatternListViewModel(), container: container)
                    } label: {
                        Text(content.summary.patternsEntryLabel)
                    }
                    NavigationLink {
                        CatalogListView(viewModel: container.makeCatalogListViewModel(), container: container)
                    } label: {
                        Text("Explore official catalog")
                    }
                }
                Section("Support") {
                    NavigationLink {
                        SupportView(viewModel: container.makeSupportViewModel(), container: container)
                    } label: {
                        Text(content.summary.supportGuidance)
                    }
                }
                Section("More") {
                    NavigationLink {
                        SettingsView(viewModel: container.makeSettingsViewModel())
                    } label: {
                        Text("Settings")
                    }
                }
            }
        }
    }
}

#Preview {
    HomeView(
        viewModel: DependencyContainer().makeHomeViewModel(),
        container: DependencyContainer()
    )
}
