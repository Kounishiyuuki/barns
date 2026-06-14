import SwiftUI

struct ItemDetailView: View {
    @StateObject private var viewModel: ItemDetailViewModel

    init(viewModel: ItemDetailViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        content
            .navigationTitle("Item")
            .navigationBarTitleDisplayMode(.inline)
            .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ProgressView()
        case .notFound:
            ContentUnavailableView("Item not found", systemImage: "questionmark")
        case .failed(let message):
            Text(message)
                .foregroundStyle(.secondary)
        case .loaded(let item):
            List {
                LabeledContent("Name", value: item.name)
                LabeledContent("Category", value: item.categoryId)
                LabeledContent("Installed place", value: item.locationLabel ?? "—")
                LabeledContent("Next care", value: "Not scheduled")
                LabeledContent("Memo", value: item.notes ?? "—")
            }
        }
    }
}

#Preview {
    NavigationStack {
        ItemDetailView(
            viewModel: DependencyContainer().makeItemDetailViewModel(itemId: "item-wall-green-001")
        )
    }
}
