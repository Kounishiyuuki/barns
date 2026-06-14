protocol SupportRepository {
    func categories() async throws -> [Category]
    func careGuides() async throws -> [CareGuide]
    func notices() async throws -> [Notice]
    func companyInfo() async throws -> CompanyInfo
}
