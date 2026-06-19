import SwiftUI

struct ItemDetailView: View {
    @StateObject private var viewModel: ItemDetailViewModel
    private let container: DependencyContainer

    init(viewModel: ItemDetailViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Registered greenery")
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
            let display = ProductItemPresentation(item: item)
            List {
                Section("Overview") {
                    LabeledContent("Name", value: display.name)
                    LabeledContent("Type", value: display.typeLabel)
                    LabeledContent("Category", value: display.categoryLabel)
                    LabeledContent("Installed place", value: display.locationLabel)
                    LabeledContent("Status", value: display.statusLabel)
                }
                Section("Care") {
                    LabeledContent("Care status", value: display.careStatusLabel)
                    Text(display.nextActionHint)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                Section("Support") {
                    NavigationLink {
                        ConsultationDraftView(viewModel: container.makeConsultationDraftViewModel(for: item))
                    } label: {
                        Label("Prepare consultation note", systemImage: "square.and.pencil")
                    }
                    Text("Gather details about this greenery before contacting support. Phone consultation guidance is available from the Support screen.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                if let notes = item.notes, !notes.isEmpty {
                    Section("Memo") {
                        Text(notes)
                    }
                }
                Section {
                    Text("This registry is kept locally on your device in the current MVP.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        ItemDetailView(
            viewModel: DependencyContainer().makeItemDetailViewModel(itemId: "item-wall-green-001"),
            container: DependencyContainer()
        )
    }
}
