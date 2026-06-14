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
                        LabeledContent("My Items", value: "\(content.summary.registeredItemCount)")
                    }
                    LabeledContent("Next care", value: content.summary.nextCareLabel)
                }
                Section("Explore") {
                    Text(content.summary.patternsEntryLabel)
                }
                Section("Support") {
                    Text(content.summary.supportGuidance)
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
