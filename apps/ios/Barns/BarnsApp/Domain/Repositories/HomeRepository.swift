protocol HomeRepository {
    func homeSummary() async throws -> HomeSummary
}
