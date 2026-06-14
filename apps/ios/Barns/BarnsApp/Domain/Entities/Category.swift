import Foundation

struct Category: Equatable, Identifiable, Sendable {
    let id: String
    let name: String
    let description: String
    let colorHex: String
    let imageUrl: URL?
}
