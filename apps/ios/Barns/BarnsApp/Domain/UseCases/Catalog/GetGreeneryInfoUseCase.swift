/// Returns official basic information for a greenery, if it exists.
struct GetGreeneryInfoUseCase {
    private let repository: GreeneryInfoRepository

    init(repository: GreeneryInfoRepository) {
        self.repository = repository
    }

    func execute(id: GreeneryInfo.ID) async throws -> GreeneryInfo? {
        try await repository.greeneryInfo(id: id)
    }
}
