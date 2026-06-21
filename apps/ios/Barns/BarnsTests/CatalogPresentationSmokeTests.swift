import XCTest
@testable import Barns

/// Smoke tests for the supporting Catalog presentation. Official read-only
/// content is loaded through use cases (not direct mock access), stays
/// separate from customer-owned ProductItem state, and is local-only with no
/// shopping/EC fields.
@MainActor
final class CatalogPresentationSmokeTests: XCTestCase {
    private func makeContainer() -> DependencyContainer { DependencyContainer() }

    func testCatalogListLoadsThroughUseCase() async {
        let viewModel = makeContainer().makeCatalogListViewModel()

        await viewModel.load()

        guard case .loaded(let items) = viewModel.state else {
            return XCTFail("Expected loaded state")
        }
        XCTAssertFalse(items.isEmpty)
        let first = items.first
        XCTAssertNotNil(first)
        XCTAssertFalse(first?.name.isEmpty ?? true)
        XCTAssertFalse(first?.kindLabel.isEmpty ?? true)
        XCTAssertFalse(first?.summary.isEmpty ?? true)
    }

    func testCatalogDetailResolvesLinkedOfficialContent() async {
        let viewModel = makeContainer().makeCatalogDetailViewModel(itemId: "catalog-wall-green-panel")

        await viewModel.load()

        guard case .loaded(let detail) = viewModel.state else {
            return XCTFail("Expected loaded state")
        }
        XCTAssertFalse(detail.name.isEmpty)
        XCTAssertFalse(detail.summary.isEmpty)
        // Linked GreeneryInfo resolved via use case.
        XCTAssertTrue(detail.hasBasicInformation)
        XCTAssertNotNil(detail.overview)
        // Linked CareGuides resolved via use case.
        XCTAssertTrue(detail.hasCareGuides)
        XCTAssertFalse(detail.careGuides.first?.title.isEmpty ?? true)
    }

    func testCatalogDetailWithoutGreeneryInfoDegradesSafely() async {
        // The maintenance kit has greeneryInfoId == nil but has a care guide.
        let viewModel = makeContainer().makeCatalogDetailViewModel(itemId: "catalog-maintenance-kit")

        await viewModel.load()

        guard case .loaded(let detail) = viewModel.state else {
            return XCTFail("Expected loaded state")
        }
        XCTAssertFalse(detail.hasBasicInformation)
        XCTAssertNil(detail.overview)
        XCTAssertTrue(detail.hasCareGuides)
    }

    func testUnknownCatalogItemIsNotFound() async {
        let viewModel = makeContainer().makeCatalogDetailViewModel(itemId: "catalog-does-not-exist")

        await viewModel.load()

        XCTAssertEqual(viewModel.state, .notFound)
    }

    func testKindLabelHumanizesToken() {
        XCTAssertEqual(CatalogKind.label(for: "wall-greening"), "Wall greening")
        XCTAssertEqual(CatalogKind.label(for: "interior-green"), "Interior green")
        XCTAssertEqual(CatalogKind.label(for: ""), "Greenery")
    }

    /// Boundary: Catalog presentation carries no shopping/EC fields and no
    /// customer-owned ProductItem state.
    func testCatalogPresentationHasNoShoppingOrOwnedFields() {
        let detailFields = Set(Mirror(reflecting: CatalogDetailContent(
            name: "n", kindLabel: "k", summary: "s",
            overview: nil, lightPreference: nil, wateringOverview: nil, careGuides: []
        )).children.compactMap { $0.label })
        let item = CatalogItem(
            id: "x", categoryId: "c", name: "n", kind: "k", summary: "s",
            greeneryInfoId: nil, careGuideIds: [], imageUrl: nil
        )
        let listFields = Set(Mirror(reflecting: CatalogPresentationItem(item: item))
            .children.compactMap { $0.label })

        for forbidden in ["price", "cart", "order", "payment", "stock", "checkout",
                          "locationLabel", "notes", "status", "consultationDraft"] {
            XCTAssertFalse(detailFields.contains(forbidden), "Forbidden field in detail: \(forbidden)")
            XCTAssertFalse(listFields.contains(forbidden), "Forbidden field in list row: \(forbidden)")
        }
    }
}
