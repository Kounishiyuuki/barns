/// Returns official care guides. With no ids, returns all guides; with ids,
/// returns the matching guides (for a catalog item's `careGuideIds`).
struct GetCareGuidesUseCase {
    private let repository: CareGuideRepository

    init(repository: CareGuideRepository) {
        self.repository = repository
    }

    func execute() async throws -> [CareGuide] {
        try await repository.careGuides()
    }

    func execute(ids: [CareGuide.ID]) async throws -> [CareGuide] {
        try await repository.careGuides(ids: ids)
    }
}
