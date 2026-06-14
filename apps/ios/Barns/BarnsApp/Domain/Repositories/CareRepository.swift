protocol CareRepository {
    func careTasks() async throws -> [CareTask]
    func careTask(id: CareTask.ID) async throws -> CareTask?
    func completeCareTask(id: CareTask.ID) async throws
    func careLogs(for productItemId: ProductItem.ID?) async throws -> [CareLog]
}
