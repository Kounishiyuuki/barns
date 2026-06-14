import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel

    init(viewModel: HomeViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
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
                    LabeledContent("Registered items", value: "\(content.summary.registeredItemCount)")
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
    HomeView(viewModel: DependencyContainer().makeHomeViewModel())
}
