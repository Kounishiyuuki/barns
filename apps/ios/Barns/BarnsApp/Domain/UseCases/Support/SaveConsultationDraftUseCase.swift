import Foundation

/// Saves the consultation draft locally. Local-only: the draft is never
/// sent to a server.
struct SaveConsultationDraftUseCase {
    private let repository: ConsultationDraftRepository

    init(repository: ConsultationDraftRepository) {
        self.repository = repository
    }

    func execute(
        existing: ConsultationDraft?,
        topic: String,
        category: ConsultationCategory,
        urgency: ConsultationUrgency,
        body: String
    ) async throws -> ConsultationDraft {
        let now = Date()
        let draft = ConsultationDraft(
            id: existing?.id ?? UUID().uuidString,
            productItemId: existing?.productItemId,
            topic: topic,
            category: category,
            urgency: urgency,
            body: body,
            status: .draft,
            createdAt: existing?.createdAt ?? now,
            updatedAt: now,
            imageUrl: nil
        )
        try await repository.saveDraft(draft)
        return draft
    }
}
