import Combine

/// Loads a catalog item detail and resolves its linked official content
/// (GreeneryInfo, CareGuides) through use cases. All linking degrades safely:
/// a missing/unknown greeneryInfoId or care guide id yields no crash and an
/// empty/none section.
@MainActor
final class CatalogDetailViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(CatalogDetailContent)
        case notFound
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let itemId: String
    private let getCatalogItemDetailUseCase: GetCatalogItemDetailUseCase
    private let getGreeneryInfoUseCase: GetGreeneryInfoUseCase
    private let getCareGuidesUseCase: GetCareGuidesUseCase

    init(
        itemId: String,
        getCatalogItemDetailUseCase: GetCatalogItemDetailUseCase,
        getGreeneryInfoUseCase: GetGreeneryInfoUseCase,
        getCareGuidesUseCase: GetCareGuidesUseCase
    ) {
        self.itemId = itemId
        self.getCatalogItemDetailUseCase = getCatalogItemDetailUseCase
        self.getGreeneryInfoUseCase = getGreeneryInfoUseCase
        self.getCareGuidesUseCase = getCareGuidesUseCase
    }

    func load() async {
        state = .loading
        do {
            guard let item = try await getCatalogItemDetailUseCase.execute(id: itemId) else {
                state = .notFound
                return
            }
            state = .loaded(await resolveContent(for: item))
        } catch {
            state = .failed("Unable to load this catalog item. Please try again.")
        }
    }

    private func resolveContent(for item: CatalogItem) async -> CatalogDetailContent {
        var overview: String?
        var lightPreference: String?
        var wateringOverview: String?
        if let infoId = item.greeneryInfoId,
           let info = try? await getGreeneryInfoUseCase.execute(id: infoId) {
            overview = info.overview
            lightPreference = info.lightPreference
            wateringOverview = info.wateringOverview
        }

        let guides = item.careGuideIds.isEmpty
            ? []
            : (try? await getCareGuidesUseCase.execute(ids: item.careGuideIds)) ?? []
        let summaries = guides.map {
            CatalogDetailContent.CareGuideSummary(id: $0.id, title: $0.title, summary: $0.summary)
        }

        return CatalogDetailContent(
            name: item.name,
            kindLabel: CatalogKind.label(for: item.kind),
            summary: item.summary,
            overview: overview,
            lightPreference: lightPreference,
            wateringOverview: wateringOverview,
            careGuides: summaries,
            registerPrefill: RegisterGreeneryPrefill(catalogItem: item),
            imageReference: item.imageUrl
        )
    }
}
