import XCTest
@testable import Barns

/// Smoke tests for the local-only Archived list + Restore flow. Restore is the
/// inverse soft action of archive: the ProductItem stays in the local store (no
/// hard delete), only its status flips from `.archived` back to `.active`,
/// official links and stable fields are preserved, and restore happens only on
/// an explicit action. Official Catalog / GreeneryInfo / CareGuide content is
/// never mutated.
@MainActor
final class RestoreGreenerySmokeTests: XCTestCase {
    private func firstItem(_ repository: MockProductItemRepository) async throws -> ProductItem {
        let items = try await repository.productItems()
        return try XCTUnwrap(items.first)
    }

    /// Archives one item, then asserts the archived list shows only archived
    /// items through the view model / use case path.
    private func archiveFirst(_ repository: MockProductItemRepository) async throws -> ProductItem {
        let item = try await firstItem(repository)
        return try await ArchiveProductItemUseCase(repository: repository).execute(item)
    }

    func testArchivedListLoadsOnlyArchivedItems() async throws {
        let repository = MockProductItemRepository()
        let archived = try await archiveFirst(repository)
        let viewModel = ArchivedGreeneryViewModel(
            getProductItemsUseCase: GetProductItemsUseCase(repository: repository),
            restoreProductItemUseCase: RestoreProductItemUseCase(repository: repository)
        )

        await viewModel.load()

        guard case .loaded(let items) = viewModel.state else { return XCTFail("Expected loaded") }
        XCTAssertTrue(items.contains { $0.id == archived.id })
        XCTAssertTrue(items.allSatisfy { $0.status == .archived })
    }

    func testRestoreSetsStatusBackToActiveOnlyOnExplicitAction() async throws {
        let repository = MockProductItemRepository()
        let archived = try await archiveFirst(repository)
        let viewModel = ArchivedGreeneryViewModel(
            getProductItemsUseCase: GetProductItemsUseCase(repository: repository),
            restoreProductItemUseCase: RestoreProductItemUseCase(repository: repository)
        )

        // Loading the archived list does not restore anything.
        await viewModel.load()
        let stillArchived = try await repository.productItem(id: archived.id)
        XCTAssertEqual(stillArchived?.status, .archived)

        // Only the explicit restore action flips the status back to active.
        let didRestore = await viewModel.restore(archived)
        XCTAssertTrue(didRestore)
        let restored = try await repository.productItem(id: archived.id)
        XCTAssertEqual(restored?.status, .active)
    }

    func testRestoreDoesNotChangeItemCount() async throws {
        let repository = MockProductItemRepository()
        let countBefore = try await repository.productItems().count
        let archived = try await archiveFirst(repository)

        _ = try await RestoreProductItemUseCase(repository: repository).execute(archived)

        // No hard delete and no re-insertion: count is stable across the
        // archive + restore round trip.
        let countAfter = try await repository.productItems().count
        XCTAssertEqual(countAfter, countBefore)
    }

    func testRestorePreservesStableFields() async throws {
        let repository = MockProductItemRepository()
        let archived = try await archiveFirst(repository)

        let restored = try await RestoreProductItemUseCase(repository: repository).execute(archived)

        XCTAssertEqual(restored.status, .active)
        XCTAssertEqual(restored.id, archived.id)
        XCTAssertEqual(restored.categoryId, archived.categoryId)
        XCTAssertEqual(restored.name, archived.name)
        XCTAssertEqual(restored.type, archived.type)
        XCTAssertEqual(restored.installedOrPurchasedAt, archived.installedOrPurchasedAt)
        XCTAssertEqual(restored.locationLabel, archived.locationLabel)
        XCTAssertEqual(restored.careGuideIds, archived.careGuideIds)
        XCTAssertEqual(restored.notes, archived.notes)
        XCTAssertEqual(restored.imageUrl, archived.imageUrl)
    }

    func testActiveListIncludesRestoredItemAfterReload() async throws {
        let repository = MockProductItemRepository()
        let archived = try await archiveFirst(repository)
        let activeList = MyItemsViewModel(
            getProductItemsUseCase: GetProductItemsUseCase(repository: repository)
        )

        // Archived item is absent from the active list.
        await activeList.load()
        guard case .loaded(let before) = activeList.state else { return XCTFail("Expected loaded") }
        XCTAssertFalse(before.contains { $0.id == archived.id })

        _ = try await RestoreProductItemUseCase(repository: repository).execute(archived)

        // After restore + reload, the active list includes it again.
        await activeList.load()
        guard case .loaded(let after) = activeList.state else { return XCTFail("Expected loaded") }
        XCTAssertTrue(after.contains { $0.id == archived.id })
        XCTAssertTrue(after.allSatisfy { $0.status == .active })
    }

    func testRestoreDoesNotMutateOfficialContent() async throws {
        let productRepository = MockProductItemRepository()
        let infoRepository = MockGreeneryInfoRepository()
        let careGuideRepository = MockCareGuideRepository()

        let infoBefore = try await infoRepository.greeneryInfo(id: "greenery-info-wall-green")
        let guidesBefore = try await careGuideRepository.careGuides()

        let archived = try await archiveFirst(productRepository)
        _ = try await RestoreProductItemUseCase(repository: productRepository).execute(archived)

        // Official read-only content is untouched by a local restore (the
        // restore path only ever touches the ProductItem repository).
        let infoAfter = try await infoRepository.greeneryInfo(id: "greenery-info-wall-green")
        let guidesAfter = try await careGuideRepository.careGuides()
        XCTAssertEqual(infoAfter, infoBefore)
        XCTAssertEqual(guidesAfter, guidesBefore)
    }

    func testExistingArchiveFlowStillWorks() async throws {
        // Restore must not break the existing archive path.
        let repository = MockProductItemRepository()
        let item = try await firstItem(repository)

        let archived = try await ArchiveProductItemUseCase(repository: repository).execute(item)
        XCTAssertEqual(archived.status, .archived)
        let stored = try await repository.productItem(id: item.id)
        XCTAssertEqual(stored?.status, .archived)
    }
}
