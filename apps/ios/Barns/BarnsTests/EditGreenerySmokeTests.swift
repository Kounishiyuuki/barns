import XCTest
@testable import Barns

/// Smoke tests for the local-only Edit Greenery flow. Editing updates the
/// customer-owned ProductItem locally only; official content is untouched and
/// nothing is saved until an explicit save.
@MainActor
final class EditGreenerySmokeTests: XCTestCase {
    private func seededItem(from repository: MockProductItemRepository) async throws -> ProductItem {
        let items = try await GetProductItemsUseCase(repository: repository).execute()
        return try XCTUnwrap(items.first)
    }

    func testEditFormInitializesFromExistingItem() async throws {
        let repository = MockProductItemRepository()
        let item = try await seededItem(from: repository)

        let viewModel = EditGreeneryViewModel(
            item: item,
            updateProductItemUseCase: UpdateProductItemUseCase(repository: repository)
        )

        XCTAssertEqual(viewModel.name, item.name)
        XCTAssertEqual(viewModel.type, item.type)
        XCTAssertEqual(viewModel.locationLabel, item.locationLabel ?? "")
        XCTAssertEqual(viewModel.status, item.status)
        XCTAssertTrue(viewModel.canSave)
    }

    func testEditingFieldsDoesNotSaveUntilExplicitSave() async throws {
        let repository = MockProductItemRepository()
        let item = try await seededItem(from: repository)

        let viewModel = EditGreeneryViewModel(
            item: item,
            updateProductItemUseCase: UpdateProductItemUseCase(repository: repository)
        )
        viewModel.name = "Renamed greenery"
        viewModel.locationLabel = "New spot"

        // No save called yet: the stored item is unchanged.
        let stored = try await repository.productItem(id: item.id)
        XCTAssertEqual(stored?.name, item.name)
        XCTAssertNotEqual(stored?.name, "Renamed greenery")
    }

    func testExplicitSaveUpdatesLocalItem() async throws {
        let repository = MockProductItemRepository()
        let item = try await seededItem(from: repository)
        let countBefore = try await GetProductItemsUseCase(repository: repository).execute().count

        let viewModel = EditGreeneryViewModel(
            item: item,
            updateProductItemUseCase: UpdateProductItemUseCase(repository: repository)
        )
        viewModel.name = "Renamed greenery"
        viewModel.locationLabel = "New spot"
        viewModel.notes = "Edited note"
        viewModel.status = .archived

        let saved = await viewModel.save()
        XCTAssertTrue(saved)

        let stored = try await repository.productItem(id: item.id)
        XCTAssertEqual(stored?.name, "Renamed greenery")
        XCTAssertEqual(stored?.locationLabel, "New spot")
        XCTAssertEqual(stored?.notes, "Edited note")
        XCTAssertEqual(stored?.status, .archived)
        // Update, not insert: count is unchanged and official links preserved.
        let countAfter = try await GetProductItemsUseCase(repository: repository).execute().count
        XCTAssertEqual(countAfter, countBefore)
        XCTAssertEqual(stored?.id, item.id)
        XCTAssertEqual(stored?.categoryId, item.categoryId)
        XCTAssertEqual(stored?.careGuideIds, item.careGuideIds)
        XCTAssertEqual(stored?.imageUrl, item.imageUrl)
    }

    func testUpdateUseCasePreservesOfficialLinksAndImageNil() async throws {
        let repository = MockProductItemRepository()
        let item = try await seededItem(from: repository)
        let update = UpdateProductItemUseCase(repository: repository)

        let updated = try await update.execute(
            original: item,
            name: "X",
            type: .purchased,
            locationLabel: nil,
            notes: nil,
            status: .active
        )

        XCTAssertEqual(updated.categoryId, item.categoryId)
        XCTAssertEqual(updated.careGuideIds, item.careGuideIds)
        XCTAssertNil(updated.imageUrl)
        XCTAssertEqual(updated.id, item.id)
    }

    func testRegisterGreeneryFlowStillAddsNewItem() async throws {
        // Editing must not break the existing add path.
        let repository = MockProductItemRepository()
        let getItems = GetProductItemsUseCase(repository: repository)
        let before = try await getItems.execute().count

        let added = try await AddProductItemUseCase(repository: repository).execute(
            name: "Brand new",
            categoryId: "cat-wall-green",
            type: .installed,
            locationLabel: nil,
            notes: nil
        )

        let after = try await getItems.execute()
        XCTAssertEqual(after.count, before + 1)
        XCTAssertTrue(after.contains { $0.id == added.id })
    }
}
