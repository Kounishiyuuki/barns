import Foundation

struct ConsultationDraft: Equatable, Identifiable, Sendable {
    let id: String
    let productItemId: String?
    let topic: String
    let category: ConsultationCategory
    let urgency: ConsultationUrgency
    let body: String
    let status: ConsultationDraftStatus
    let createdAt: Date
    let updatedAt: Date
    let imageUrl: URL?
}

enum ConsultationCategory: String, Equatable, Sendable {
    case maintenance
    case care
    case replacement
    case other
}

enum ConsultationUrgency: String, Equatable, Sendable {
    case low
    case normal
    case high
}

enum ConsultationDraftStatus: String, Equatable, Sendable {
    case draft
    case archived
}
