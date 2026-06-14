import SwiftUI

struct CareTaskDetailView: View {
    @StateObject private var viewModel: CareTaskDetailViewModel

    init(viewModel: CareTaskDetailViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        content
            .navigationTitle("Care task")
            .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ProgressView()
        case .notFound:
            ContentUnavailableView("Task not found", systemImage: "questionmark")
        case .failed(let message):
            Text(message)
                .foregroundStyle(.secondary)
        case .loaded(let task):
            List {
                Section {
                    LabeledContent("Title", value: task.title)
                    LabeledContent("Related item", value: task.productItemId)
                    LabeledContent("Scheduled", value: task.dueDate.formatted(date: .abbreviated, time: .omitted))
                    LabeledContent("Description", value: description(for: task.careType))
                    LabeledContent("Status", value: task.status.rawValue.capitalized)
                }
                Section {
                    Button {
                        Task { await viewModel.complete() }
                    } label: {
                        Text(task.status == .completed ? "Completed" : "Mark as completed")
                    }
                    .disabled(task.status == .completed || viewModel.isCompleting)
                }
            }
        }
    }

    private func description(for careType: CareType) -> String {
        switch careType {
        case .watering: return "Water this item."
        case .cleaning: return "Clean this item."
        case .pruning: return "Prune this item."
        case .inspection: return "Inspect this item's condition."
        case .replacement: return "Replace this item or its parts."
        case .other: return "General care."
        }
    }
}

#Preview {
    NavigationStack {
        CareTaskDetailView(
            viewModel: DependencyContainer().makeCareTaskDetailViewModel(taskId: "care-task-001")
        )
    }
}
