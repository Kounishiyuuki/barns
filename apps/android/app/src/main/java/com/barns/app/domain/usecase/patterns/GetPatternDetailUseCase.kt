package com.barns.app.domain.usecase.patterns

import com.barns.app.domain.model.WallGreenPattern
import com.barns.app.domain.repository.PatternRepository

/**
 * Returns a single wall-greenery pattern by id, if present.
 */
class GetPatternDetailUseCase(
    private val repository: PatternRepository,
) {
    suspend fun execute(id: String): WallGreenPattern? = repository.wallGreenPattern(id)
}
