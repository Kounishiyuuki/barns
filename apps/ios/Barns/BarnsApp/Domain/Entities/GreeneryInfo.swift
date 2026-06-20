import Foundation

/// Official, read-only basic information about a greenery type. Concise and
/// UI-friendly for a future basic-information screen.
///
/// Boundary: official shared content only. No user-owned state.
struct GreeneryInfo: Equatable, Identifiable, Sendable {
    let id: String
    let name: String
    let overview: String
    let difficulty: String
    let recommendedEnvironment: String
    let lightPreference: String
    let wateringOverview: String
    let maintenanceNotes: String
    let imageUrl: URL?
}
