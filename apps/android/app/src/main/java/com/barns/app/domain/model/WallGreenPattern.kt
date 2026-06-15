package com.barns.app.domain.model

data class WallGreenPattern(
    val id: String,
    val name: String,
    val recommendedSpace: String,
    val mood: String,
    val maintenanceLevel: MaintenanceLevel,
    val description: String,
    val imageUrl: String?,
)

enum class MaintenanceLevel { LOW, STANDARD, MEDIUM, HIGH }
