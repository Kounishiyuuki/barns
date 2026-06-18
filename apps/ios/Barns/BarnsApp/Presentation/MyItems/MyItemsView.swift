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
            .navigationTitle("My Greenery")
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
                    "Register your greenery",
                    systemImage: "leaf",
                    description: Text("Add the wall greening and interior green you own or had installed to keep their care and support in one place.")
                )
            } else {
                List {
                    Section {
                        ForEach(items) { item in
                            let display = ProductItemPresentation(item: item)
                            NavigationLink {
                                ItemDetailView(viewModel: container.makeItemDetailViewModel(itemId: item.id))
                            } label: {
                                itemCard(display)
                            }
                        }
                    } footer: {
                        Text("Your greenery registry stays on this device.")
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func itemCard(_ display: ProductItemPresentation) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(display.name)
                .font(.headline)
            Text(display.ownershipSummary)
                .font(.subheadline)
                .foregroundStyle(.secondary)
            HStack(spacing: 6) {
                Label(display.categoryLabel, systemImage: "leaf")
                Text("·")
                Label(display.locationLabel, systemImage: "mappin.and.ellipse")
            }
            .font(.caption)
            .foregroundStyle(.secondary)
            Text(display.careStatusLabel)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .padding(.vertical, 2)
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
