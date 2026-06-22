/// Presentation-only prefill for the Register Greenery flow, derived from an
/// official `CatalogItem` so the user can start registering greenery they
/// already have.
///
/// Boundary: this is a prefill source only. It copies official, non-personal
/// fields (name, category, a compatible type) — never customer-owned state.
/// It does not mutate the CatalogItem, does not link it, and does not create a
/// ProductItem. The user must still confirm and save through the existing
/// Register Greenery flow.
struct RegisterGreeneryPrefill: Equatable {
    let name: String
    let categoryId: String
    let type: ProductItemType

    init(name: String, categoryId: String, type: ProductItemType) {
        self.name = name
        self.categoryId = categoryId
        self.type = type
    }

    init(catalogItem: CatalogItem) {
        name = catalogItem.name
        categoryId = catalogItem.categoryId
        // Wall greening is typically installed; other greenery is typically
        // owned. This is only an initial value the user can change.
        type = catalogItem.categoryId == "cat-wall-green" ? .installed : .purchased
    }
}
