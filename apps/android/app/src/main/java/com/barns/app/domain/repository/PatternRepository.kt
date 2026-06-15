package com.barns.app.domain.repository

import com.barns.app.domain.model.WallGreenPattern

interface PatternRepository {
    suspend fun wallGreenPatterns(): List<WallGreenPattern>
    suspend fun wallGreenPattern(id: String): WallGreenPattern?
}
