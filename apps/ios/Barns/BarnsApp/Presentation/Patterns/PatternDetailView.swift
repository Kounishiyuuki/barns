import SwiftUI

struct PatternDetailView: View {
    @StateObject private var viewModel: PatternDetailViewModel

    init(viewModel: PatternDetailViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        content
            .navigationTitle("Pattern")
            .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ProgressView()
        case .notFound:
            ContentUnavailableView("Pattern not found", systemImage: "questionmark")
        case .failed(let message):
            Text(message)
                .foregroundStyle(.secondary)
        case .loaded(let pattern):
            List {
                Section {
                    LabeledContent("Title", value: pattern.name)
                    LabeledContent("Recommended place", value: pattern.recommendedSpace)
                    LabeledContent("Difficulty", value: pattern.maintenanceLevel.rawValue.capitalized)
                }
                Section("Description") {
                    Text(pattern.description)
                }
                Section("Care guide") {
                    Text("See the care guides for upkeep details.")
                        .foregroundStyle(.secondary)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        PatternDetailView(
            viewModel: DependencyContainer().makePatternDetailViewModel(patternId: "pattern-calm-grid")
        )
    }
}
