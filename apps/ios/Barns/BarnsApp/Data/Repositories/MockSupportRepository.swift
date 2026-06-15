/// In-memory, local-only support content. No persistence, no network.
/// Company info is fake and non-sensitive; phone/address are null.
/// Server-served content (categories, care guides, notices) is out of
/// scope for this flow and returned empty for now.
actor MockSupportRepository: SupportRepository {
    func categories() async throws -> [Category] {
        []
    }

    func careGuides() async throws -> [CareGuide] {
        []
    }

    func notices() async throws -> [Notice] {
        []
    }

    func companyInfo() async throws -> CompanyInfo {
        CompanyInfo(
            id: "company-info-default",
            displayName: "barns support",
            description: "After-purchase support for wall greenery and interior green.",
            inquiryPolicy: "For the MVP, external inquiries are guided to phone consultation.",
            phoneLabel: "Call for consultation",
            phoneNumber: nil,
            address: nil,
            businessHoursNote: "Business hours will be set after company confirmation.",
            imageUrl: nil
        )
    }
}
