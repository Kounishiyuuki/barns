package com.barns.app.data.repository

import com.barns.app.domain.model.MaintenanceLevel
import com.barns.app.domain.model.WallGreenPattern
import com.barns.app.domain.repository.PatternRepository

/**
 * In-memory, local-only wall-greenery patterns. No persistence, no network.
 * Seed data is fake, non-sensitive, and image-null compatible.
 */
class MockPatternRepository : PatternRepository {
    private val patterns = listOf(
        WallGreenPattern(
            id = "pattern-calm-grid",
            name = "Calm grid",
            recommendedSpace = "Reception, meeting room",
            mood = "Orderly, refined, quiet",
            maintenanceLevel = MaintenanceLevel.STANDARD,
            description = "A deep-green base in a straight grid layout for a calm impression.",
            imageUrl = null,
        ),
        WallGreenPattern(
            id = "pattern-natural-flow",
            name = "Natural flow",
            recommendedSpace = "Store, salon, shared space",
            mood = "Natural, soft, friendly",
            maintenanceLevel = MaintenanceLevel.MEDIUM,
            description = "Varied leaf shapes and tones for a natural sense of depth.",
            imageUrl = null,
        ),
    )

    override suspend fun wallGreenPatterns(): List<WallGreenPattern> = patterns

    override suspend fun wallGreenPattern(id: String): WallGreenPattern? =
        patterns.firstOrNull { it.id == id }
}
