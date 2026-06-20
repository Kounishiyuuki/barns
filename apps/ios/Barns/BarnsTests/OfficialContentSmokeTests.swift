import XCTest
@testable import Barns

/// Smoke tests for the official, read-only content foundation: Catalog,
/// GreeneryInfo, and CareGuide. Local-only, no network, no persistence.
final class OfficialContentSmokeTests: XCTestCase {
    func testCatalogItemsAreListed() async throws {
        let getItems = GetCatalogItemsUseCase(repository: MockCatalogRepository())

        let items = try await getItems.execute()

        XCTAssertFalse(items.isEmpty)
        // Official content has no image in the MVP.
        XCTAssertTrue(items.allSatisfy { $0.imageUrl == nil })
    }

    func testCatalogItemReferencesResolve() async throws {
        let catalog = MockCatalogRepository()
        let greeneryRepo = MockGreeneryInfoRepository()
        let careRepo = MockCareGuideRepository()

        let getDetail = GetCatalogItemDetailUseCase(repository: catalog)
        let getInfo = GetGreeneryInfoUseCase(repository: greeneryRepo)
        let getGuides = GetCareGuidesUseCase(repository: careRepo)

        let fetched = try await getDetail.execute(id: "catalog-wall-green-panel")
        let item = try XCTUnwrap(fetched)

        // greeneryInfoId resolves when present.
        let infoId = try XCTUnwrap(item.greeneryInfoId)
        let info = try await getInfo.execute(id: infoId)
        XCTAssertNotNil(info)

        // careGuideIds resolve.
        XCTAssertFalse(item.careGuideIds.isEmpty)
        let guides = try await getGuides.execute(ids: item.careGuideIds)
        XCTAssertEqual(guides.count, item.careGuideIds.count)
    }

    func testEveryCatalogReferenceResolves() async throws {
        let catalog = MockCatalogRepository()
        let greeneryRepo = MockGreeneryInfoRepository()
        let careRepo = MockCareGuideRepository()

        let items = try await GetCatalogItemsUseCase(repository: catalog).execute()
        let getInfo = GetGreeneryInfoUseCase(repository: greeneryRepo)
        let getGuides = GetCareGuidesUseCase(repository: careRepo)

        for item in items {
            if let infoId = item.greeneryInfoId {
                let info = try await getInfo.execute(id: infoId)
                XCTAssertNotNil(info, "Missing GreeneryInfo for \(infoId)")
            }
            let guides = try await getGuides.execute(ids: item.careGuideIds)
            XCTAssertEqual(
                guides.count,
                item.careGuideIds.count,
                "Unresolved care guide for \(item.id)"
            )
        }
    }

    func testGreeneryInfoUnknownIdReturnsNil() async throws {
        let getInfo = GetGreeneryInfoUseCase(repository: MockGreeneryInfoRepository())
        let info = try await getInfo.execute(id: "greenery-info-does-not-exist")
        XCTAssertNil(info)
    }

    func testCareGuidesListAndLookup() async throws {
        let repo = MockCareGuideRepository()
        let all = try await repo.careGuides()
        XCTAssertFalse(all.isEmpty)

        let one = try await repo.careGuide(id: "guide-watering-basic")
        XCTAssertEqual(one?.id, "guide-watering-basic")

        let subset = try await repo.careGuides(ids: ["guide-cleaning-basic", "missing-id"])
        XCTAssertEqual(subset.map(\.id), ["guide-cleaning-basic"])
    }

    /// Guards the data boundary: CatalogItem must not carry user-owned state.
    /// This is a compile-time-style assertion on the entity's shape — if any
    /// of these properties existed, this test file would fail to compile.
    func testCatalogItemHasNoCustomerOwnedFields() {
        let mirror = Mirror(reflecting: CatalogItem(
            id: "x",
            categoryId: "c",
            name: "n",
            kind: "k",
            summary: "s",
            greeneryInfoId: nil,
            careGuideIds: [],
            imageUrl: nil
        ))
        let fields = Set(mirror.children.compactMap { $0.label })
        XCTAssertEqual(
            fields,
            ["id", "categoryId", "name", "kind", "summary", "greeneryInfoId", "careGuideIds", "imageUrl"]
        )
        for forbidden in ["locationLabel", "notes", "status", "careLogs", "consultationDraft"] {
            XCTAssertFalse(fields.contains(forbidden), "Customer-owned field leaked: \(forbidden)")
        }
    }
}
