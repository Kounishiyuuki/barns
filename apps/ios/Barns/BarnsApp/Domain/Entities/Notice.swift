import Foundation

struct Notice: Equatable, Identifiable, Sendable {
    let id: String
    let title: String
    let body: String
    let publishedAt: Date
    let priority: NoticePriority
    let imageUrl: URL?
}

enum NoticePriority: String, Equatable, Sendable {
    case low
    case normal
    case high
}
