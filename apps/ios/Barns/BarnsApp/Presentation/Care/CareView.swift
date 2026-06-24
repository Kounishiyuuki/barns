import SwiftUI

struct CareView: View {
    @StateObject private var viewModel: CareViewModel
    private let container: DependencyContainer

    init(viewModel: CareViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Care")
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
        case .loaded(let care):
            List {
                Section("Upcoming") {
                    if care.upcoming.isEmpty {
                        Text("No upcoming care tasks. Care you plan for your My Greenery appears here.")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(care.upcoming) { task in
                            NavigationLink {
                                CareTaskDetailView(viewModel: container.makeCareTaskDetailViewModel(taskId: task.id))
                            } label: {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(task.title)
                                        .font(.headline)
                                    Text("Due \(task.dueDate.formatted(date: .abbreviated, time: .omitted))")
                                        .font(.subheadline)
                                        .foregroundStyle(.secondary)
                                }
                            }
                        }
                    }
                }
                Section("Recent care log") {
                    if care.recentLogs.isEmpty {
                        Text("No care logged yet. Completed care is recorded here on this device.")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(care.recentLogs) { log in
                            Text(log.performedAt.formatted(date: .abbreviated, time: .omitted))
                        }
                    }
                }
                Section {
                    Text("Care tasks and logs are local records for your My Greenery. barns does not send reminders or notifications, and nothing is synced.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        CareView(
            viewModel: DependencyContainer().makeCareViewModel(),
            container: DependencyContainer()
        )
    }
}
