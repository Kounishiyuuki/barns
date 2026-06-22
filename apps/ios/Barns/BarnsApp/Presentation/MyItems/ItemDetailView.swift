import SwiftUI

struct ItemDetailView: View {
    @StateObject private var viewModel: ItemDetailViewModel
    private let container: DependencyContainer
    @State private var editingItem: ProductItem?

    init(viewModel: ItemDetailViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Registered greenery")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if case .loaded(let item) = viewModel.state {
                    ToolbarItem(placement: .primaryAction) {
                        Button("Edit") { editingItem = item }
                    }
                }
            }
            .sheet(item: $editingItem, onDismiss: { Task { await viewModel.load() } }) { item in
                NavigationStack {
                    EditGreeneryView(viewModel: container.makeEditGreeneryViewModel(item: item))
                }
            }
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
                if let official = viewModel.officialContent {
                    officialContentSections(official)
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

    /// Official, read-only reference sections shown as supporting after-care
    /// information. Not personalized and not submitted anywhere.
    @ViewBuilder
    private func officialContentSections(_ official: ItemOfficialContent) -> some View {
        if official.hasBasicInformation {
            Section {
                if let overview = official.overview {
                    Text(overview)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                if let light = official.lightPreference {
                    LabeledContent("Light", value: light)
                }
                if let watering = official.wateringOverview {
                    LabeledContent("Watering", value: watering)
                }
            } header: {
                Text("Basic information")
            } footer: {
                Text("Official reference information.")
            }
        }
        if official.hasCareGuides {
            Section("Care guide") {
                ForEach(official.careGuides) { guide in
                    VStack(alignment: .leading, spacing: 2) {
                        Text(guide.title)
                            .font(.subheadline)
                        Text(guide.summary)
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
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
