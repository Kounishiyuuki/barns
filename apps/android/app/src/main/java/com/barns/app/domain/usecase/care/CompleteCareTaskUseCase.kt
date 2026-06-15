package com.barns.app.domain.usecase.care

import com.barns.app.domain.repository.CareRepository

/**
 * Marks a care task as completed in local state.
 */
class CompleteCareTaskUseCase(
    private val repository: CareRepository,
) {
    suspend fun execute(id: String) = repository.completeCareTask(id)
}
