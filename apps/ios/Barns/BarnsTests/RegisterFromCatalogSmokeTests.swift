import XCTest
@testable import Barns

/// Smoke tests for the local-only "Register from Catalog" prefill flow.
/// CatalogItem is a prefill source only; no customer-owned state is copied,
/// nothing is saved without an explicit save call, and no EC behavior exists.
@MainActor
final class RegisterFromCatalogSmokeTests: XCTestCase {
    private func wallGreenCatalogItem() -> CatalogItem {
        CatalogItem(
            id: "catalog-wall-green-panel",
            categoryId: "cat-wall-green",
            name: "Lobby wall greenery panel",
            kind: "wall-greening",
            summary: "Official reference",
            greeneryInfoId: "greenery-info-wall-green",
            careGuideIds: ["guide-wall-green-basic"],
            imageUrl: nil
        )
    }

    func testPrefillCopiesOfficialFieldsOnly() {
        let item = wallGreenCatalogItem()

        let prefill = RegisterGreeneryPrefill(catalogItem: item)

        XCTAssertEqual(prefill.name, item.name)
        XCTAssertEqual(prefill.categoryId, item.categoryId)
        // Wall greening maps to installed as an initial, editable value.
        XCTAssertEqual(prefill.type, .installed)
    }

    func testPrefillTypeForNonWallGreenIsOwned() {
        let item = CatalogItem(
            id: "catalog-desk-planter",
            categoryId: "cat-interior-green",
            name: "Desk planter",
            kind: "interior-green",
            summary: "Official reference",
            greeneryInfoId: "greenery-info-desk-planter",
            careGuideIds: [],
            imageUrl: nil
        )

        XCTAssertEqual(RegisterGreeneryPrefill(catalogItem: item).type, .purchased)
    }

    func testPrefillHasNoCustomerOwnedFields() {
        let mirror = Mirror(reflecting: RegisterGreeneryPrefill(catalogItem: wallGreenCatalogItem()))
        let fields = Set(mirror.children.compactMap { $0.label })
        XCTAssertEqual(fields, ["name", "categoryId", "type"])
        for forbidden in ["locationLabel", "notes", "status", "careLogs", "consultationDraft", "imageUrl"] {
            XCTAssertFalse(fields.contains(forbidden), "Customer-owned/official field leaked: \(forbidden)")
        }
    }

    func testAddItemViewModelAppliesPrefillButDoesNotAutoSave() async {
        let repository = MockProductItemRepository()
        let getItems = GetProductItemsUseCase(repository: repository)
        let before = try? await getItems.execute().count

        let viewModel = AddItemViewModel(
            addProductItemUseCase: AddProductItemUseCase(repository: repository),
            prefill: RegisterGreeneryPrefill(catalogItem: wallGreenCatalogItem())
        )

        // Prefill is applied to the editable form.
        XCTAssertEqual(viewModel.name, "Lobby wall greenery panel")
        XCTAssertEqual(viewModel.type, .installed)
        XCTAssertTrue(viewModel.canSave)

        // Constructing the view model must NOT have saved anything.
        let afterConstruct = try? await getItems.execute().count
        XCTAssertEqual(afterConstruct, before)
    }

    func testExplicitSavePersistsLocallyWithPrefilledCategory() async throws {
        let repository = MockProductItemRepository()
        let getItems = GetProductItemsUseCase(repository: repository)
        let before = try await getItems.execute().count

        let viewModel = AddItemViewModel(
            addProductItemUseCase: AddProductItemUseCase(repository: repository),
            prefill: RegisterGreeneryPrefill(catalogItem: wallGreenCatalogItem())
        )

        // Only an explicit save writes to My Greenery.
        let saved = await viewModel.save()
        XCTAssertTrue(saved)

        let after = try await getItems.execute()
        XCTAssertEqual(after.count, before + 1)
        let added = try XCTUnwrap(after.first { $0.name == "Lobby wall greenery panel" })
        XCTAssertEqual(added.categoryId, "cat-wall-green")
        XCTAssertEqual(added.type, .installed)
        XCTAssertNil(added.imageUrl)
    }

    func testCatalogDetailContentExposesPrefill() async {
        let viewModel = DependencyContainer().makeCatalogDetailViewModel(itemId: "catalog-wall-green-panel")

        await viewModel.load()

        guard case .loaded(let detail) = viewModel.state else {
            return XCTFail("Expected loaded state")
        }
        XCTAssertEqual(detail.registerPrefill.name, detail.name)
        XCTAssertFalse(detail.registerPrefill.categoryId.isEmpty)
    }
}
