import SwiftUI

struct MyItemsView: View {
    @StateObject private var viewModel: MyItemsViewModel
    private let container: DependencyContainer
    @State private var isAddingItem = false

    init(viewModel: MyItemsViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("My Items")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        isAddingItem = true
                    } label: {
                        Label("Add item", systemImage: "plus")
                    }
                }
            }
            .sheet(isPresented: $isAddingItem, onDismiss: { Task { await viewModel.load() } }) {
                NavigationStack {
                    AddItemView(viewModel: container.makeAddItemViewModel())
                }
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
        case .loaded(let items):
            if items.isEmpty {
                ContentUnavailableView(
                    "No items yet",
                    systemImage: "leaf",
                    description: Text("Your registered greenery will appear here.")
                )
            } else {
                List(items) { item in
                    NavigationLink {
                        ItemDetailView(viewModel: container.makeItemDetailViewModel(itemId: item.id))
                    } label: {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(item.name)
                                .font(.headline)
                            if let location = item.locationLabel {
                                Text(location)
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        MyItemsView(
            viewModel: DependencyContainer().makeMyItemsViewModel(),
            container: DependencyContainer()
        )
    }
}
