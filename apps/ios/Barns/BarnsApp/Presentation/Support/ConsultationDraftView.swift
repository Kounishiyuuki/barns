import SwiftUI

/// Local-only consultation draft input. The draft is never sent to a server.
struct ConsultationDraftView: View {
    @StateObject private var viewModel: ConsultationDraftViewModel

    init(viewModel: ConsultationDraftViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        Form {
            if let itemName = viewModel.itemContextName {
                Section {
                    LabeledContent("For", value: itemName)
                    Text("Preparing a local support note for this registered greenery. It stays on your device and is not submitted.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
            Section("Topic") {
                TextField("Topic", text: $viewModel.topic)
            }
            Section("Category") {
                Picker("Category", selection: $viewModel.category) {
                    ForEach(viewModel.categories, id: \.self) { category in
                        Text(category.rawValue.capitalized).tag(category)
                    }
                }
                Picker("Urgency", selection: $viewModel.urgency) {
                    ForEach(viewModel.urgencies, id: \.self) { urgency in
                        Text(urgency.rawValue.capitalized).tag(urgency)
                    }
                }
            }
            Section("Details") {
                TextField("Details", text: $viewModel.body, axis: .vertical)
            }
            Section {
                Button("Save draft") {
                    Task { await viewModel.save() }
                }
                .disabled(!viewModel.canSave)
                Text("Saved locally only. Not sent to any server.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
        .navigationTitle("Consultation draft")
        .task { await viewModel.load() }
    }
}

#Preview {
    NavigationStack {
        ConsultationDraftView(viewModel: DependencyContainer().makeConsultationDraftViewModel())
    }
}
