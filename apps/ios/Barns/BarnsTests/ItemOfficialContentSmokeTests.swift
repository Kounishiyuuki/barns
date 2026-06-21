import XCTest
@testable import Barns

/// Smoke tests for the My Greenery detail official-content sections. Official
/// read-only content is resolved through use cases (not direct mock access),
/// stays separate from customer-owned ProductItem state, and is local-only.
@MainActor
final class ItemOfficialContentSmokeTests: XCTestCase {
    private func makeViewModel(itemId: String) -> ItemDetailViewModel {
        let productRepo = MockProductItemRepository()
        let greeneryRepo = MockGreeneryInfoRepository()
        let careRepo = MockCareGuideRepository()
        return ItemDetailViewModel(
            itemId: itemId,
            getProductItemDetailUseCase: GetProductItemDetailUseCase(repository: productRepo),
            getGreeneryInfoUseCase: GetGreeneryInfoUseCase(repository: greeneryRepo),
            getCareGuidesUseCase: GetCareGuidesUseCase(repository: careRepo)
        )
    }

    func testWallGreenItemResolvesBasicInfoAndCareGuides() async {
        let viewModel = makeViewModel(itemId: "item-wall-green-001")

        await viewModel.load()

        guard case .loaded = viewModel.state else {
            return XCTFail("Expected loaded state")
        }
        let official = try? XCTUnwrap(viewModel.officialContent)
        XCTAssertNotNil(official)
        // Basic information resolved via the GreeneryInfo use case.
        XCTAssertTrue(official?.hasBasicInformation == true)
        XCTAssertNotNil(official?.overview)
        XCTAssertNotNil(official?.lightPreference)
        XCTAssertNotNil(official?.wateringOverview)
        // Care guide summaries resolved via the care-guide use case.
        XCTAssertTrue(official?.hasCareGuides == true)
        XCTAssertFalse((official?.careGuides ?? []).isEmpty)
        XCTAssertFalse(official?.careGuides.first?.title.isEmpty ?? true)
    }

    func testCareGuidesResolveThroughUseCaseForCategoryDefaults() async {
        // Seed items have empty careGuideIds, so the category resolver supplies
        // the official defaults — proving resolution is not from ProductItem.
        let viewModel = makeViewModel(itemId: "item-interior-green-001")

        await viewModel.load()

        let guides = viewModel.officialContent?.careGuides ?? []
        XCTAssertFalse(guides.isEmpty)
        XCTAssertTrue(guides.contains { $0.id == "guide-watering-basic" })
    }

    func testUnknownItemHasNoOfficialContent() async {
        let viewModel = makeViewModel(itemId: "does-not-exist")

        await viewModel.load()

        XCTAssertEqual(viewModel.state, .notFound)
        XCTAssertNil(viewModel.officialContent)
    }

    func testOfficialContentLinkResolverIsCategoryBased() {
        let wall = OfficialContentLink.resolve(categoryId: "cat-wall-green")
        XCTAssertEqual(wall.greeneryInfoId, "greenery-info-wall-green")
        XCTAssertFalse(wall.careGuideIds.isEmpty)

        let interior = OfficialContentLink.resolve(categoryId: "cat-interior-green")
        XCTAssertEqual(interior.greeneryInfoId, "greenery-info-interior-foliage")

        // Maintenance supplies have care guidance but no basic-info entry.
        let supply = OfficialContentLink.resolve(categoryId: "cat-maintenance-supply")
        XCTAssertNil(supply.greeneryInfoId)
        XCTAssertFalse(supply.careGuideIds.isEmpty)

        // Unknown categories degrade safely to no official content.
        let unknown = OfficialContentLink.resolve(categoryId: "cat-unknown")
        XCTAssertNil(unknown.greeneryInfoId)
        XCTAssertTrue(unknown.careGuideIds.isEmpty)
    }

    func testOfficialContentIsSeparateFromProductItemState() async {
        // Official content carries only reference fields; the ProductItem's
        // own local state (location, notes) is not part of it.
        let viewModel = makeViewModel(itemId: "item-wall-green-001")
        await viewModel.load()

        let official = viewModel.officialContent
        let mirror = Mirror(reflecting: ItemOfficialContent(
            overview: nil,
            lightPreference: nil,
            wateringOverview: nil,
            careGuides: []
        ))
        let fields = Set(mirror.children.compactMap { $0.label })
        for forbidden in ["locationLabel", "notes", "status", "productItemId"] {
            XCTAssertFalse(fields.contains(forbidden))
        }
        XCTAssertNotNil(official)
    }
}
