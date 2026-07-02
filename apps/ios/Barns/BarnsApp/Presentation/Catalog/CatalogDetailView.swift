import SwiftUI

/// Concise, official read-only catalog detail. Reference content only — no
/// price, stock, cart, order, or registration actions in this screen.
struct CatalogDetailView: View {
    @StateObject private var viewModel: CatalogDetailViewModel
    private let container: DependencyContainer

    init(viewModel: CatalogDetailViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Catalog item")
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
        case .loaded(let detail):
            List {
                if let heroImage = LocalMockImage.uiImage(for: detail.imageReference) {
                    Section {
                        Image(uiImage: heroImage)
                            .resizable()
                            .scaledToFill()
                            .frame(maxWidth: .infinity)
                            .frame(height: 200)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .accessibilityHidden(true)
                    } footer: {
                        Text("Mock demo reference image. Not a real product or customer photo.")
                    }
                }
                Section {
                    LabeledContent("Name", value: detail.name)
                    LabeledContent("Kind", value: detail.kindLabel)
                    Text(detail.summary)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                } header: {
                    Text("Overview")
                } footer: {
                    Text("Official read-only reference content.")
                }
                if detail.hasBasicInformation {
                    Section("Basic information") {
                        if let overview = detail.overview {
                            Text(overview)
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }
                        if let light = detail.lightPreference {
                            LabeledContent("Light", value: light)
                        }
                        if let watering = detail.wateringOverview {
                            LabeledContent("Watering", value: watering)
                        }
                    }
                }
                if detail.hasCareGuides {
                    Section("Care guide") {
                        ForEach(detail.careGuides) { guide in
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
                Section {
                    NavigationLink {
                        AddItemView(viewModel: container.makeAddItemViewModel(prefill: detail.registerPrefill))
                    } label: {
                        Label("Register to My Greenery", systemImage: "plus.circle")
                    }
                } footer: {
                    Text("Already have this greenery? Add it to your local My Greenery registry. This creates a local entry only — nothing is ordered, reserved, or submitted.")
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        CatalogDetailView(
            viewModel: DependencyContainer().makeCatalogDetailViewModel(itemId: "catalog-wall-green-panel"),
            container: DependencyContainer()
        )
    }
}
