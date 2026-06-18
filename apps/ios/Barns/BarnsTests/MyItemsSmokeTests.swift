import XCTest
@testable import Barns

/// Smoke tests for the local-only My Items flow.
final class MyItemsSmokeTests: XCTestCase {
    func testSeededItemsAreListed() async throws {
        let repository = MockProductItemRepository()
        let getItems = GetProductItemsUseCase(repository: repository)

        let items = try await getItems.execute()

        XCTAssertFalse(items.isEmpty)
    }

    func testAddItemAppendsLocally() async throws {
        let repository = MockProductItemRepository()
        let getItems = GetProductItemsUseCase(repository: repository)
        let addItem = AddProductItemUseCase(repository: repository)

        let before = try await getItems.execute().count
        let added = try await addItem.execute(
            name: "Test planter",
            categoryId: "cat-wall-green",
            locationLabel: "Test area",
            notes: nil
        )
        let after = try await getItems.execute()

        XCTAssertEqual(after.count, before + 1)
        XCTAssertTrue(after.contains { $0.id == added.id })
        XCTAssertEqual(added.name, "Test planter")
        XCTAssertNil(added.imageUrl)
    }

    func testRegisterInstalledGreenerySetsType() async throws {
        let repository = MockProductItemRepository()
        let addItem = AddProductItemUseCase(repository: repository)

        let registered = try await addItem.execute(
            name: "Lobby wall greenery",
            categoryId: "cat-wall-green",
            type: .installed,
            locationLabel: "Entrance wall",
            notes: nil
        )

        XCTAssertEqual(registered.type, .installed)
        // Newly registered greenery is active and local-only (no image, no remote id).
        XCTAssertEqual(registered.status, .active)
        XCTAssertNil(registered.imageUrl)
    }

    @MainActor
    func testAddItemViewModelDefaultsToInstalledAndRegisters() async throws {
        let repository = MockProductItemRepository()
        let viewModel = AddItemViewModel(
            addProductItemUseCase: AddProductItemUseCase(repository: repository)
        )

        // Registration framing: installed greenery is the default type.
        XCTAssertEqual(viewModel.type, .installed)

        let getItems = GetProductItemsUseCase(repository: repository)
        let before = try await getItems.execute().count

        viewModel.name = "Window planter"
        XCTAssertTrue(viewModel.canSave)
        let saved = await viewModel.save()

        XCTAssertTrue(saved)
        let after = try await getItems.execute()
        XCTAssertEqual(after.count, before + 1)
        XCTAssertTrue(after.contains { $0.name == "Window planter" && $0.type == .installed })
    }

    @MainActor
    func testInstalledItemPresentationFramesOwnershipAndSupport() {
        let item = ProductItem(
            id: "item-1",
            categoryId: "cat-wall-green",
            name: "Lobby wall greenery",
            type: .installed,
            installedOrPurchasedAt: nil,
            locationLabel: "Entrance wall",
            status: .active,
            careGuideIds: [],
            notes: nil,
            imageUrl: nil,
            updatedAt: nil
        )

        let display = ProductItemPresentation(item: item)

        XCTAssertEqual(display.ownershipSummary, "Installed greenery in your care")
        XCTAssertEqual(display.typeLabel, "Installed greenery")
        XCTAssertEqual(display.categoryLabel, "Wall greenery")
        XCTAssertEqual(display.locationLabel, "Entrance wall")
        XCTAssertEqual(display.statusLabel, "In your care")
        // No linked care guide yet -> next-action guidance is offered.
        XCTAssertEqual(display.careStatusLabel, "No care guide linked yet")
        XCTAssertFalse(display.nextActionHint.isEmpty)
    }

    @MainActor
    func testPresentationFallsBackForMissingLocationAndUnknownCategory() {
        let item = ProductItem(
            id: "item-2",
            categoryId: "cat-roof-garden",
            name: "Rooftop planter",
            type: .purchased,
            installedOrPurchasedAt: nil,
            locationLabel: nil,
            status: .active,
            careGuideIds: ["guide-1"],
            notes: nil,
            imageUrl: nil,
            updatedAt: nil
        )

        let display = ProductItemPresentation(item: item)

        XCTAssertEqual(display.ownershipSummary, "Greenery you own")
        XCTAssertEqual(display.categoryLabel, "Roof garden")
        XCTAssertEqual(display.locationLabel, "Location not set")
        XCTAssertEqual(display.careStatusLabel, "Care guidance linked")
    }
}
