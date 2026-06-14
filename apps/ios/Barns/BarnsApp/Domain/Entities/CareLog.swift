import Foundation

struct CareLog: Equatable, Identifiable, Sendable {
    let id: String
    let productItemId: String
    let careTaskId: String?
    let careType: CareType
    let performedAt: Date
    let memo: String?
    let imageUrl: URL?
}
