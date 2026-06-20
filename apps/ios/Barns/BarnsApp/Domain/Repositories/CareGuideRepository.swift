/// Official, read-only care/growing guidance. Mock now; a real read-only API
/// may back this later without changing use cases or screens.
protocol CareGuideRepository {
    func careGuides() async throws -> [CareGuide]
    func careGuide(id: CareGuide.ID) async throws -> CareGuide?
    func careGuides(ids: [CareGuide.ID]) async throws -> [CareGuide]
}
