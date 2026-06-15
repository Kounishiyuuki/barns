/// In-memory, local-only wall-greenery patterns. No persistence, no network.
/// Seed data is fake, non-sensitive, and image-null compatible.
actor MockPatternRepository: PatternRepository {
    private let patterns: [WallGreenPattern] = [
        WallGreenPattern(
            id: "pattern-calm-grid",
            name: "Calm grid",
            recommendedSpace: "Reception, meeting room",
            mood: "Orderly, refined, quiet",
            maintenanceLevel: .standard,
            description: "A deep-green base in a straight grid layout for a calm impression.",
            imageUrl: nil
        ),
        WallGreenPattern(
            id: "pattern-natural-flow",
            name: "Natural flow",
            recommendedSpace: "Store, salon, shared space",
            mood: "Natural, soft, friendly",
            maintenanceLevel: .medium,
            description: "Varied leaf shapes and tones for a natural sense of depth.",
            imageUrl: nil
        )
    ]

    func wallGreenPatterns() async throws -> [WallGreenPattern] {
        patterns
    }

    func wallGreenPattern(id: WallGreenPattern.ID) async throws -> WallGreenPattern? {
        patterns.first { $0.id == id }
    }
}
