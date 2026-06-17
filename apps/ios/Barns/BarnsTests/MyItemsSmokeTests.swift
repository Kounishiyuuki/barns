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
}
