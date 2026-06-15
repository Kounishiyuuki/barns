/// Returns the user's current local consultation draft, if any.
struct GetConsultationDraftUseCase {
    private let repository: ConsultationDraftRepository

    init(repository: ConsultationDraftRepository) {
        self.repository = repository
    }

    func execute() async throws -> ConsultationDraft? {
        try await repository.currentDraft()
    }
}
