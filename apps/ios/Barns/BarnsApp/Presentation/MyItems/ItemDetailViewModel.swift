import Combine

@MainActor
final class ItemDetailViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(ProductItem)
        case notFound
        case failed(String)
    }

    @Published private(set) var state: State = .loading
    /// Official read-only reference content (basic info + care guides) for the
    /// loaded item. `nil` until resolved; stays `nil` if nothing matches.
    @Published private(set) var officialContent: ItemOfficialContent?

    private let itemId: ProductItem.ID
    private let getProductItemDetailUseCase: GetProductItemDetailUseCase
    private let getGreeneryInfoUseCase: GetGreeneryInfoUseCase
    private let getCareGuidesUseCase: GetCareGuidesUseCase

    init(
        itemId: ProductItem.ID,
        getProductItemDetailUseCase: GetProductItemDetailUseCase,
        getGreeneryInfoUseCase: GetGreeneryInfoUseCase,
        getCareGuidesUseCase: GetCareGuidesUseCase
    ) {
        self.itemId = itemId
        self.getProductItemDetailUseCase = getProductItemDetailUseCase
        self.getGreeneryInfoUseCase = getGreeneryInfoUseCase
        self.getCareGuidesUseCase = getCareGuidesUseCase
    }

    func load() async {
        state = .loading
        officialContent = nil
        do {
            if let item = try await getProductItemDetailUseCase.execute(id: itemId) {
                state = .loaded(item)
                officialContent = await resolveOfficialContent(for: item)
            } else {
                state = .notFound
            }
        } catch {
            state = .failed("Unable to load this item. Please try again.")
        }
    }

    /// Resolves official basic information and care guides for the item.
    ///
    /// Linking is intentionally minimal: `ProductItem` has no `greeneryInfoId`
    /// yet, so a registered item is mapped to official content by its
    /// `categoryId` via `OfficialContentLink`. This is a documented temporary
    /// resolver — when `ProductItem` later carries an explicit official-content
    /// id, only this method changes. Official content is loaded through use
    /// cases (never direct repository/JSON access). Failures degrade to `nil`.
    private func resolveOfficialContent(for item: ProductItem) async -> ItemOfficialContent? {
        let link = OfficialContentLink.resolve(categoryId: item.categoryId)

        var overview: String?
        var lightPreference: String?
        var wateringOverview: String?
        if let infoId = link.greeneryInfoId,
           let info = try? await getGreeneryInfoUseCase.execute(id: infoId) {
            overview = info.overview
            lightPreference = info.lightPreference
            wateringOverview = info.wateringOverview
        }

        // Prefer the item's own care guides when present; otherwise fall back
        // to the category's default official guides.
        let guideIds = item.careGuideIds.isEmpty ? link.careGuideIds : item.careGuideIds
        let guides = (try? await getCareGuidesUseCase.execute(ids: guideIds)) ?? []
        let summaries = guides.map {
            ItemOfficialContent.CareGuideSummary(id: $0.id, title: $0.title, summary: $0.summary)
        }

        let content = ItemOfficialContent(
            overview: overview,
            lightPreference: lightPreference,
            wateringOverview: wateringOverview,
            careGuides: summaries
        )
        return content.isEmpty ? nil : content
    }
}
