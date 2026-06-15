/// In-memory, local-only consultation draft. No persistence, no network.
/// The draft is private and is never sent to a server.
actor MockConsultationDraftRepository: ConsultationDraftRepository {
    private var draft: ConsultationDraft?

    func currentDraft() async throws -> ConsultationDraft? {
        draft
    }

    func saveDraft(_ draft: ConsultationDraft) async throws {
        self.draft = draft
    }
}
