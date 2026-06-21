/// Concise, presentation-only view of the official read-only content shown as
/// supporting after-care reference on a registered greenery's detail screen.
///
/// Boundary: this is built from official `GreeneryInfo` / `CareGuide` content
/// only. It carries no customer-owned `ProductItem` state and is never
/// submitted anywhere — it is reference information, not personalized data.
struct ItemOfficialContent: Equatable {
    struct CareGuideSummary: Equatable, Identifiable {
        let id: String
        let title: String
        let summary: String
    }

    /// Official basic information (from `GreeneryInfo`), when available.
    let overview: String?
    let lightPreference: String?
    let wateringOverview: String?
    /// A short list of official care-guide summaries (kept to a few rows).
    let careGuides: [CareGuideSummary]

    var hasBasicInformation: Bool {
        overview != nil || lightPreference != nil || wateringOverview != nil
    }

    var hasCareGuides: Bool { !careGuides.isEmpty }

    var isEmpty: Bool { !hasBasicInformation && !hasCareGuides }
}
