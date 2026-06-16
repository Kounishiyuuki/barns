package com.barns.app.domain.usecase.patterns

import com.barns.app.domain.model.WallGreenPattern
import com.barns.app.domain.repository.PatternRepository

/**
 * Returns the local wall-greenery patterns.
 */
class GetPatternsUseCase(
    private val repository: PatternRepository,
) {
    suspend fun execute(): List<WallGreenPattern> = repository.wallGreenPatterns()
}
