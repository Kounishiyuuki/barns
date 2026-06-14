import SwiftUI

struct AddItemView: View {
    @StateObject private var viewModel: AddItemViewModel
    @Environment(\.dismiss) private var dismiss

    init(viewModel: AddItemViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        Form {
            Section("Item") {
                TextField("Name", text: $viewModel.name)
                TextField("Installed place", text: $viewModel.locationLabel)
            }
            Section("Memo") {
                TextField("Memo", text: $viewModel.notes, axis: .vertical)
            }
            if let errorMessage = viewModel.errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
                    .font(.footnote)
            }
        }
        .navigationTitle("Add item")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Cancel") { dismiss() }
            }
            ToolbarItem(placement: .confirmationAction) {
                Button("Add") {
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
