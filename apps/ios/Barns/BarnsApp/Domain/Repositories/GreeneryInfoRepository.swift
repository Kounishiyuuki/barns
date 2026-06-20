/// Official, read-only greenery basic information. Mock now; a real read-only
/// API may back this later without changing use cases or screens.
protocol GreeneryInfoRepository {
    func greeneryInfo(id: GreeneryInfo.ID) async throws -> GreeneryInfo?
}
