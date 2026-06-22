import SwiftUI

/// Supporting, official read-only catalog list. Reference content only — no
/// price, stock, cart, or order actions.
struct CatalogListView: View {
    @StateObject private var viewModel: CatalogListViewModel
    private let container: DependencyContainer

    init(viewModel: CatalogListViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Official catalog")
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
        case .loaded(let items):
            List {
                Section {
                    ForEach(items) { item in
                        NavigationLink {
                            CatalogDetailView(
                                viewModel: container.makeCatalogDetailViewModel(itemId: item.id),
                                container: container
                            )
                        } label: {
                            VStack(alignment: .leading, spacing: 2) {
                                Text(item.name)
                                    .font(.headline)
                                Text(item.kindLabel)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                                Text(item.summary)
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                } footer: {
                    Text("Official reference content. Browse only — no ordering in the app.")
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        CatalogListView(
            viewModel: DependencyContainer().makeCatalogListViewModel(),
            container: DependencyContainer()
        )
    }
}
