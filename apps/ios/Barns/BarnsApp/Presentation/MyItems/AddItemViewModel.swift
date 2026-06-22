import Combine
import Foundation

@MainActor
final class AddItemViewModel: ObservableObject {
    @Published var name: String = ""
    // Registering installed greenery is the primary flow, so default to it.
    @Published var type: ProductItemType = .installed
    @Published var locationLabel: String = ""
    @Published var notes: String = ""
    @Published private(set) var isSaving: Bool = false
    @Published private(set) var errorMessage: String?

    // MVP source skeleton: defaults to a placeholder category until full
    // category selection exists. A Catalog prefill may override it. Local-only;
    // never sent to a server.
    private var categoryId = "cat-wall-green"

    private let addProductItemUseCase: AddProductItemUseCase

    init(addProductItemUseCase: AddProductItemUseCase, prefill: RegisterGreeneryPrefill? = nil) {
        self.addProductItemUseCase = addProductItemUseCase
        if let prefill {
            name = prefill.name
            categoryId = prefill.categoryId
            type = prefill.type
        }
    }

    var canSave: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && !isSaving
    }

    /// Adds the item locally. Returns true on success so the view can dismiss.
    func save() async -> Bool {
        guard canSave else { return false }
        isSaving = true
        errorMessage = nil
        defer { isSaving = false }
        do {
            _ = try await addProductItemUseCase.execute(
                name: name.trimmingCharacters(in: .whitespacesAndNewlines),
                categoryId: categoryId,
                type: type,
                locationLabel: emptyToNil(locationLabel),
                notes: emptyToNil(notes)
            )
            return true
        } catch {
            errorMessage = "Unable to add the item. Please try again."
            return false
        }
    }

    private func emptyToNil(_ value: String) -> String? {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }
}
