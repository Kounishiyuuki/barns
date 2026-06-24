import SwiftUI

/// Local-only Archived Greenery list. Shows the customer's archived greenery
/// and offers a single restore action per item (confirmed via an alert).
/// Read-only otherwise: no hard delete, no bulk actions, no sort/filter UI.
struct ArchivedGreeneryView: View {
    @StateObject private var viewModel: ArchivedGreeneryViewModel
    @State private var itemPendingRestore: ProductItem?

    init(viewModel: ArchivedGreeneryViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        content
            .navigationTitle("Archived Greenery")
            .navigationBarTitleDisplayMode(.inline)
            .alert(
                "Restore to My Greenery",
                isPresented: Binding(
                    get: { itemPendingRestore != nil },
                    set: { if !$0 { itemPendingRestore = nil } }
                ),
                presenting: itemPendingRestore
            ) { item in
                Button("Restore") {
                    Task { await viewModel.restore(item) }
                    itemPendingRestore = nil
                }
                Button("Cancel", role: .cancel) { itemPendingRestore = nil }
            } message: { item in
                Text("\(item.name) will return to your active My Greenery list on this device.")
            }
            .onAppear { Task { await viewModel.load() } }
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
                    "No archived greenery",
                    systemImage: "archivebox",
                    description: Text("Greenery you archive is kept here on this device. You can restore it to My Greenery anytime.")
                )
            } else {
                List {
                    Section {
                        ForEach(items) { item in
                            archivedRow(item)
                        }
                    } footer: {
                        Text("Archived greenery stays on this device and is never deleted.")
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func archivedRow(_ item: ProductItem) -> some View {
        let display = ProductItemPresentation(item: item)
        HStack(alignment: .top) {
            GreenerySummaryView(display: display)
            Spacer(minLength: 12)
            Button("Restore") { itemPendingRestore = item }
                .buttonStyle(.bordered)
                .font(.caption)
        }
        .padding(.vertical, 2)
    }
}

#Preview {
    NavigationStack {
        ArchivedGreeneryView(
            viewModel: DependencyContainer().makeArchivedGreeneryViewModel()
        )
    }
}
