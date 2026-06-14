import Foundation

struct WallGreenPattern: Equatable, Identifiable, Sendable {
    let id: String
    let name: String
    let recommendedSpace: String
    let mood: String
    let maintenanceLevel: MaintenanceLevel
    let description: String
    let imageUrl: URL?
}

enum MaintenanceLevel: String, Equatable, Sendable {
    case low
    case standard
    case medium
    case high
}
