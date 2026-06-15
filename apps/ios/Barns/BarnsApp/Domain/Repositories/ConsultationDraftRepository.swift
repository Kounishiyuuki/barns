/// Local-only storage for the user's consultation draft.
/// Drafts are private and must never be sent to a server.
protocol ConsultationDraftRepository {
    func currentDraft() async throws -> ConsultationDraft?
    func saveDraft(_ draft: ConsultationDraft) async throws
}
