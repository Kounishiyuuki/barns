import Foundation

struct CareTask: Equatable, Identifiable, Sendable {
    let id: String
    let productItemId: String
    let title: String
    let careType: CareType
    let dueDate: Date
    let repeatRule: CareRepeatRule?
    let status: CareTaskStatus
    let reminderEnabled: Bool
    let imageUrl: URL?
}

enum CareType: String, Equatable, Sendable {
    case watering
    case cleaning
    case pruning
    case inspection
    case replacement
    case other
}

enum CareRepeatRule: String, Equatable, Sendable {
    case daily
    case weekly
    case monthly
    case seasonal
}

enum CareTaskStatus: String, Equatable, Sendable {
    case pending
    case completed
    case skipped
}
