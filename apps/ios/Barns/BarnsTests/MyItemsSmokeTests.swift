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
