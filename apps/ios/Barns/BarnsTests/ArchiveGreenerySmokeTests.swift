import XCTest
@testable import Barns

/// Smoke tests for the local-only Archive Greenery flow. Archiving is a soft
/// action: the ProductItem stays in the local store (no hard delete), only its
/// status changes, official links are preserved, and the active list excludes
/// archived items.
@MainActor
final class ArchiveGreenerySmokeTests: XCTestCase {
    private func firstItem(_ repository: MockProductItemRepository) async throws -> ProductItem {
        let items = try await repository.productItems()
        return try XCTUnwrap(items.first)
    }

    func testArchiveKeepsItemInStoreAndSetsStatus() async throws {
        let repository = MockProductItemRepository()
        let item = try await firstItem(repository)
        let countBefore = try await repository.productItems().count

        let archived = try await ArchiveProductItemUseCase(repository: repository).execute(item)

        XCTAssertEqual(archived.status, .archived)
        // No hard delete: the item is still present, count unchanged.
        let countAfter = try await repository.productItems().count
        XCTAssertEqual(countAfter, countBefore)
        let stored = try await repository.productItem(id: item.id)
        XCTAssertEqual(stored?.status, .archived)
        XCTAssertNotNil(stored)
    }

    func testArchivePreservesOfficialLinksAndIdentity() async throws {
        let repository = MockProductItemRepository()
        let item = try await firstItem(repository)

        let archived = try await ArchiveProductItemUseCase(repository: repository).execute(item)

        XCTAssertEqual(archived.id, item.id)
        XCTAssertEqual(archived.categoryId, item.categoryId)
        XCTAssertEqual(archived.careGuideIds, item.careGuideIds)
        XCTAssertEqual(archived.imageUrl, item.imageUrl)
        XCTAssertEqual(archived.name, item.name)
    }

    func testActiveListExcludesArchivedItems() async throws {
        let repository = MockProductItemRepository()
        let item = try await firstItem(repository)
        let viewModel = MyItemsViewModel(
            getProductItemsUseCase: GetProductItemsUseCase(repository: repository)
        )

        await viewModel.load()
        guard case .loaded(let before) = viewModel.state else { return XCTFail("Expected loaded") }
        XCTAssertTrue(before.contains { $0.id == item.id })

        _ = try await ArchiveProductItemUseCase(repository: repository).execute(item)
        await viewModel.load()

        guard case .loaded(let after) = viewModel.state else { return XCTFail("Expected loaded") }
        XCTAssertFalse(after.contains { $0.id == item.id })
        XCTAssertTrue(after.allSatisfy { $0.status == .active })
    }

    func testArchiveOnlyHappensThroughExplicitAction() async throws {
        let repository = MockProductItemRepository()
        let viewModel = ItemDetailViewModel(
            itemId: "item-wall-green-001",
            getProductItemDetailUseCase: GetProductItemDetailUseCase(repository: repository),
            getGreeneryInfoUseCase: GetGreeneryInfoUseCase(repository: MockGreeneryInfoRepository()),
            getCareGuidesUseCase: GetCareGuidesUseCase(repository: MockCareGuideRepository()),
            archiveProductItemUseCase: ArchiveProductItemUseCase(repository: repository)
        )

        // Loading the detail does not archive anything.
        await viewModel.load()
        let stored = try await repository.productItem(id: "item-wall-green-001")
        XCTAssertEqual(stored?.status, .active)

        // Only the explicit archive action changes the status.
        let didArchive = await viewModel.archive()
        XCTAssertTrue(didArchive)
        let afterArchive = try await repository.productItem(id: "item-wall-green-001")
        XCTAssertEqual(afterArchive?.status, .archived)
    }

    func testEditFlowStillUpdatesItem() async throws {
        // Archive must not break the existing edit path.
        let repository = MockProductItemRepository()
        let item = try await firstItem(repository)
        let updated = try await UpdateProductItemUseCase(repository: repository).execute(
            original: item,
            name: "Edited",
            type: item.type,
            locationLabel: item.locationLabel,
            notes: item.notes,
            status: item.status
        )
        XCTAssertEqual(updated.name, "Edited")
        let stored = try await repository.productItem(id: item.id)
        XCTAssertEqual(stored?.name, "Edited")
    }
}
