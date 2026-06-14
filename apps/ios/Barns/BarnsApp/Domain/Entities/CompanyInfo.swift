import Foundation

struct CompanyInfo: Equatable, Identifiable, Sendable {
    let id: String
    let displayName: String
    let description: String
    let inquiryPolicy: String
    let phoneLabel: String
    let phoneNumber: String?
    let address: String?
    let businessHoursNote: String?
    let imageUrl: URL?
}
