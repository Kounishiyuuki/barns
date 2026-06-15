import Combine
import Foundation

@MainActor
final class ConsultationDraftViewModel: ObservableObject {
    @Published var topic: String = ""
    @Published var body: String = ""
    @Published var category: ConsultationCategory = .maintenance
    @Published var urgency: ConsultationUrgency = .normal
    @Published private(set) var isSaving = false
    @Published private(set) var savedAt: Date?

    // Selectable options for the local-only draft form.
    let categories: [ConsultationCategory] = [.maintenance, .care, .replacement, .other]
    let urgencies: [ConsultationUrgency] = [.low, .normal, .high]

    private var existing: ConsultationDraft?
    private let getConsultationDraftUseCase: GetConsultationDraftUseCase
    private let saveConsultationDraftUseCase: SaveConsultationDraftUseCase

    init(
        getConsultationDraftUseCase: GetConsultationDraftUseCase,
        saveConsultationDraftUseCase: SaveConsultationDraftUseCase
    ) {
        self.getConsultationDraftUseCase = getConsultationDraftUseCase
        self.saveConsultationDraftUseCase = saveConsultationDraftUseCase
    }

    var canSave: Bool {
        !topic.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && !isSaving
    }

    func load() async {
        guard let draft = try? await getConsultationDraftUseCase.execute() else { return }
        existing = draft
        topic = draft.topic
        body = draft.body
        category = draft.category
        urgency = draft.urgency
    }

    /// Saves the draft locally only. It is never sent to a server.
    func save() async {
        guard canSave else { return }
        isSaving = true
        defer { isSaving = false }
        let saved = try? await saveConsultationDraftUseCase.execute(
            existing: existing,
            topic: topic.trimmingCharacters(in: .whitespacesAndNewlines),
            category: category,
            urgency: urgency,
            body: body.trimmingCharacters(in: .whitespacesAndNewlines)
        )
        if let saved {
            existing = saved
            savedAt = saved.updatedAt
        }
    }
}
