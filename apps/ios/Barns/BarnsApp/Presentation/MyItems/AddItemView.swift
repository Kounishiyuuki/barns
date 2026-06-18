import SwiftUI

struct AddItemView: View {
    @StateObject private var viewModel: AddItemViewModel
    @Environment(\.dismiss) private var dismiss

    init(viewModel: AddItemViewModel) {
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
            } footer: {
                Text("Register the wall greening or interior green you own or had installed.")
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
            Section {
                Text("Your registry is kept locally on this device in the current MVP. Care and support guidance stay in one place.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
            if let errorMessage = viewModel.errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
                    .font(.footnote)
            }
        }
        .navigationTitle("Register Greenery")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Cancel") { dismiss() }
            }
            ToolbarItem(placement: .confirmationAction) {
                Button("Register") {
                    Task {
                        if await viewModel.save() { dismiss() }
                    }
                }
                .disabled(!viewModel.canSave)
            }
        }
    }
}

#Preview {
    NavigationStack {
        AddItemView(viewModel: DependencyContainer().makeAddItemViewModel())
    }
}
