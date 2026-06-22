import Combine
import Foundation

/// Form state for editing an existing registered greenery (`ProductItem`).
/// Local-only: edits are kept in this presentation state until the user
/// explicitly saves; cancelling simply discards this view model.
@MainActor
final class EditGreeneryViewModel: ObservableObject {
    @Published var name: String
    @Published var type: ProductItemType
    @Published var locationLabel: String
    @Published var notes: String
    @Published var status: ProductItemStatus
    @Published private(set) var isSaving = false
    @Published private(set) var errorMessage: String?

    private let original: ProductItem
    private let updateProductItemUseCase: UpdateProductItemUseCase

    init(item: ProductItem, updateProductItemUseCase: UpdateProductItemUseCase) {
        self.original = item
        self.updateProductItemUseCase = updateProductItemUseCase
        name = item.name
        type = item.type
        locationLabel = item.locationLabel ?? ""
        notes = item.notes ?? ""
        status = item.status
    }

    var canSave: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && !isSaving
    }

    /// Updates the item locally. Returns true on success so the view can dismiss.
    func save() async -> Bool {
        guard canSave else { return false }
        isSaving = true
        errorMessage = nil
        defer { isSaving = false }
        do {
            _ = try await updateProductItemUseCase.execute(
                original: original,
                name: name.trimmingCharacters(in: .whitespacesAndNewlines),
                type: type,
                locationLabel: emptyToNil(locationLabel),
                notes: emptyToNil(notes),
                status: status
            )
            return true
        } catch {
            errorMessage = "Unable to save changes. Please try again."
            return false
        }
    }

    private func emptyToNil(_ value: String) -> String? {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty ? nil : trimmed
    }
}
