import SwiftUI

/// Local-only edit form for a registered greenery. Edits customer-owned
/// fields only; official Catalog / GreeneryInfo / CareGuide content is never
/// changed here. Changes are saved only when the user taps Save.
struct EditGreeneryView: View {
    @StateObject private var viewModel: EditGreeneryViewModel
    @Environment(\.dismiss) private var dismiss

    init(viewModel: EditGreeneryViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        Form {
            Section {
                TextField("Greenery name", text: $viewModel.name)
                Picker("Registration type", selection: $viewModel.type) {
                    Text("Installed greenery").tag(ProductItemType.installed)
                    Text("Owned greenery").tag(ProductItemType.purchased)
                }
            } header: {
                Text("Greenery")
            }
            Section {
                TextField("Installation or placement", text: $viewModel.locationLabel)
            } header: {
                Text("Where it lives")
            } footer: {
                Text("For example, the room or wall where this greenery is placed.")
            }
            Section("Memo") {
                TextField("Notes for your own reference", text: $viewModel.notes, axis: .vertical)
            }
            Section("Status") {
                Picker("Status", selection: $viewModel.status) {
                    Text("In your care").tag(ProductItemStatus.active)
                    Text("Archived").tag(ProductItemStatus.archived)
                }
            }
            Section {
                Text("Changes are saved locally on this device. Official catalog and care content is not changed.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
            if let errorMessage = viewModel.errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
                    .font(.footnote)
            }
        }
        .navigationTitle("Edit Greenery")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Cancel") { dismiss() }
            }
            ToolbarItem(placement: .confirmationAction) {
                Button("Save") {
                    Task {
                        if await viewModel.save() { dismiss() }
                    }
                }
                .disabled(!viewModel.canSave)
            }
        }
    }
}
