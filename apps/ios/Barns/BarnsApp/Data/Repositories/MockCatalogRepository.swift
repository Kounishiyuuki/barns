import Foundation

/// In-memory, official read-only catalog items. No persistence, no network.
/// Seed data mirrors shared/mock-data/catalog-items.json; fake and image-null.
///
/// Boundary: official content only. No user-owned `ProductItem` state.
actor MockCatalogRepository: CatalogRepository {
    private let items: [CatalogItem] = [
        CatalogItem(
            id: "catalog-wall-green-panel",
            categoryId: "cat-wall-green",
            name: "壁面グリーンパネル（標準）",
            kind: "wall-greening",
            summary: "壁面を彩る標準的なグリーンパネルの参考情報。設置候補の一例。",
            greeneryInfoId: "greenery-info-wall-green",
            careGuideIds: ["guide-wall-green-basic", "guide-cleaning-basic"],
            imageUrl: URL(string: "mock://catalog/catalog-office-vertical-green-wall-01")
        ),
        CatalogItem(
            id: "catalog-interior-foliage-pot",
            categoryId: "cat-interior-green",
            name: "室内観葉グリーン（鉢植え）",
            kind: "interior-green",
            summary: "受付や共有スペース向けの観葉グリーンの参考情報。",
            greeneryInfoId: "greenery-info-interior-foliage",
            careGuideIds: ["guide-watering-basic", "guide-planter-sunlight-basic"],
            imageUrl: URL(string: "mock://catalog/catalog-reception-greenery-wall-01")
        ),
        CatalogItem(
            id: "catalog-desk-planter",
            categoryId: "cat-interior-green",
            name: "デスクプランター（小型）",
            kind: "interior-green",
            summary: "机上や棚に置きやすい小型プランターの参考情報。",
            greeneryInfoId: "greenery-info-desk-planter",
            careGuideIds: ["guide-watering-basic", "guide-seasonal-care-basic"],
            imageUrl: URL(string: "mock://catalog/catalog-compact-framed-moss-panel-01")
        ),
        CatalogItem(
            id: "catalog-maintenance-kit",
            categoryId: "cat-maintenance-supply",
            name: "グリーンメンテナンス用品（基本）",
            kind: "maintenance-supply",
            summary: "日常の清掃や手入れに使う関連用品の参考情報。",
            greeneryInfoId: nil,
            careGuideIds: ["guide-cleaning-basic"],
            imageUrl: nil
        )
    ]

    func catalogItems() async throws -> [CatalogItem] {
        items
    }

    func catalogItem(id: CatalogItem.ID) async throws -> CatalogItem? {
        items.first { $0.id == id }
    }
}
