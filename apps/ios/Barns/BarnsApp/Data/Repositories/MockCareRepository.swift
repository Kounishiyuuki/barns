import Foundation

/// In-memory, local-only care data. No persistence, no network.
/// Seed data is fake, non-sensitive, and image-null compatible.
actor MockCareRepository: CareRepository {
    private var tasks: [CareTask]
    private let logs: [CareLog]

    init() {
        let now = Date()
        let day: TimeInterval = 60 * 60 * 24

        tasks = [
            CareTask(
                id: "care-task-001",
                productItemId: "item-wall-green-001",
                title: "Inspect the entryway green wall",
                careType: .inspection,
                dueDate: now.addingTimeInterval(2 * day),
                repeatRule: .monthly,
                status: .pending,
                reminderEnabled: false,
                imageUrl: nil
            ),
            CareTask(
                id: "care-task-002",
                productItemId: "item-interior-green-001",
                title: "Water the reception planter",
                careType: .watering,
                dueDate: now.addingTimeInterval(5 * day),
                repeatRule: .weekly,
                status: .pending,
                reminderEnabled: false,
                imageUrl: nil
            )
        ]

        logs = [
            CareLog(
                id: "care-log-001",
                productItemId: "item-wall-green-001",
                careTaskId: nil,
                careType: .cleaning,
                performedAt: now.addingTimeInterval(-7 * day),
                memo: "Wiped dust from the panel leaves.",
                imageUrl: nil
            )
        ]
    }

    func careTasks() async throws -> [CareTask] {
        tasks
    }

    func careTask(id: CareTask.ID) async throws -> CareTask? {
        tasks.first { $0.id == id }
    }

    func completeCareTask(id: CareTask.ID) async throws {
        guard let index = tasks.firstIndex(where: { $0.id == id }) else { return }
        let task = tasks[index]
        tasks[index] = CareTask(
            id: task.id,
            productItemId: task.productItemId,
            title: task.title,
            careType: task.careType,
            dueDate: task.dueDate,
            repeatRule: task.repeatRule,
            status: .completed,
            reminderEnabled: task.reminderEnabled,
            imageUrl: task.imageUrl
        )
    }

    func careLogs(for productItemId: ProductItem.ID?) async throws -> [CareLog] {
        guard let productItemId else { return logs }
        return logs.filter { $0.productItemId == productItemId }
    }
}
