import XCTest
@testable import Barns

/// Smoke tests for the local-only consultation draft flow, including starting
/// a draft from a registered greenery item detail. No network, no submission.
final class ConsultationDraftSmokeTests: XCTestCase {
    private func makeItem() -> ProductItem {
        ProductItem(
            id: "item-wall-green-001",
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
    }

    func testSaveDraftLinksProductItemLocally() async throws {
        let repository = MockConsultationDraftRepository()
        let save = SaveConsultationDraftUseCase(repository: repository)

        let draft = try await save.execute(
            existing: nil,
            productItemId: "item-wall-green-001",
            topic: "Leaves drooping",
            category: .care,
            urgency: .normal,
            body: "Some context"
        )

        XCTAssertEqual(draft.productItemId, "item-wall-green-001")
        // Drafts stay in draft status; there is no submitted state.
        XCTAssertEqual(draft.status, .draft)
        XCTAssertNil(draft.imageUrl)
    }

    @MainActor
    func testItemContextDraftPrefillsAndLinks() async throws {
        let repository = MockConsultationDraftRepository()
        let item = makeItem()
        let viewModel = ConsultationDraftViewModel(
            getConsultationDraftUseCase: GetConsultationDraftUseCase(repository: repository),
            saveConsultationDraftUseCase: SaveConsultationDraftUseCase(repository: repository),
            item: item
        )

        // The note is contextualized with the registered greenery.
        XCTAssertEqual(viewModel.itemContextName, item.name)

        await viewModel.load()
        XCTAssertTrue(viewModel.topic.contains(item.name))
        XCTAssertTrue(viewModel.body.contains(item.name))
        XCTAssertTrue(viewModel.canSave)

        await viewModel.save()
        XCTAssertNotNil(viewModel.savedAt)

        // The persisted local draft is linked to the item.
        let get = GetConsultationDraftUseCase(repository: repository)
        let saved = try await get.execute()
        XCTAssertEqual(saved?.productItemId, item.id)
    }

    @MainActor
    func testGeneralDraftHasNoProductItem() async throws {
        let repository = MockConsultationDraftRepository()
        let viewModel = ConsultationDraftViewModel(
            getConsultationDraftUseCase: GetConsultationDraftUseCase(repository: repository),
            saveConsultationDraftUseCase: SaveConsultationDraftUseCase(repository: repository)
        )

        XCTAssertNil(viewModel.itemContextName)

        await viewModel.load()
        viewModel.topic = "General question"
        await viewModel.save()

        let get = GetConsultationDraftUseCase(repository: repository)
        let saved = try await get.execute()
        XCTAssertNil(saved?.productItemId)
        XCTAssertEqual(saved?.status, .draft)
    }
}
