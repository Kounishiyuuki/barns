/// Returns company support info for the Support screen.
struct GetSupportInfoUseCase {
    private let repository: SupportRepository

    init(repository: SupportRepository) {
        self.repository = repository
    }

    func execute() async throws -> CompanyInfo {
        try await repository.companyInfo()
    }
}
