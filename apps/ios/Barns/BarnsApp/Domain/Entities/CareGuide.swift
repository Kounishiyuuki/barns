import Foundation

struct CareGuide: Equatable, Identifiable, Sendable {
    let id: String
    let title: String
    let categoryId: String
    let summary: String
    let steps: [String]
    let frequency: String
    let cautions: [String]
    let imageUrl: URL?
}
