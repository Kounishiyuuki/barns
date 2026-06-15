import SwiftUI

struct PatternListView: View {
    @StateObject private var viewModel: PatternListViewModel
    private let container: DependencyContainer

    init(viewModel: PatternListViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Patterns")
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
        case .loaded(let patterns):
            List(patterns) { pattern in
                NavigationLink {
                    PatternDetailView(viewModel: container.makePatternDetailViewModel(patternId: pattern.id))
                } label: {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(pattern.name)
                            .font(.headline)
                        Text(pattern.recommendedSpace)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        PatternListView(
            viewModel: DependencyContainer().makePatternListViewModel(),
            container: DependencyContainer()
        )
    }
}
