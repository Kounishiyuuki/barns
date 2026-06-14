protocol CareRepository {
    func careTasks() async throws -> [CareTask]
    func careLogs(for productItemId: ProductItem.ID?) async throws -> [CareLog]
}
