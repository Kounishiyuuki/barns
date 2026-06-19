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

    /// When the draft is started from a registered greenery item detail, this
    /// holds the item context so the note can be prefilled. Optional so the
    /// general Support draft flow keeps working unchanged.
    private let itemContext: ProductItem?
    /// Human-readable name of the registered greenery this note is about.
    let itemContextName: String?

    private var existing: ConsultationDraft?
    private let getConsultationDraftUseCase: GetConsultationDraftUseCase
    private let saveConsultationDraftUseCase: SaveConsultationDraftUseCase

    init(
        getConsultationDraftUseCase: GetConsultationDraftUseCase,
        saveConsultationDraftUseCase: SaveConsultationDraftUseCase,
        item: ProductItem? = nil
    ) {
        self.getConsultationDraftUseCase = getConsultationDraftUseCase
        self.saveConsultationDraftUseCase = saveConsultationDraftUseCase
        self.itemContext = item
        self.itemContextName = item?.name
    }

    var canSave: Bool {
        !topic.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && !isSaving
    }

    func load() async {
        // Item-specific note: prefill from the registered greenery context.
        // Local-only; nothing is submitted anywhere.
        if let item = itemContext {
            if topic.isEmpty {
                topic = "Consultation: \(item.name)"
            }
            if body.isEmpty {
                body = Self.contextSummary(for: item)
            }
            return
        }

        guard let draft = try? await getConsultationDraftUseCase.execute() else { return }
        existing = draft
        topic = draft.topic
        body = draft.body
        category = draft.category
        urgency = draft.urgency
    }

    /// Builds a local-only context summary from existing item fields so the
    /// user can add their concern below it before contacting support.
    private static func contextSummary(for item: ProductItem) -> String {
        let display = ProductItemPresentation(item: item)
        return """
        Item: \(display.name)
        Type: \(display.typeLabel)
        Category: \(display.categoryLabel)
        Location: \(display.locationLabel)
        Status: \(display.statusLabel)

        Concern:\u{0020}
        """
    }

    /// Saves the draft locally only. It is never sent to a server.
    func save() async {
        guard canSave else { return }
        isSaving = true
        defer { isSaving = false }
        let saved = try? await saveConsultationDraftUseCase.execute(
            existing: existing,
            productItemId: itemContext?.id,
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
