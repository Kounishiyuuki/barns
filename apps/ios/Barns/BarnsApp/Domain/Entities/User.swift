import Foundation

struct User: Equatable, Identifiable, Sendable {
    let id: String
    let displayName: String
}
