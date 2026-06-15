package com.barns.app.domain.usecase.care

import com.barns.app.domain.model.CareTask
import com.barns.app.domain.repository.CareRepository

/**
 * Returns the user's local care tasks.
 */
class GetCareTasksUseCase(
    private val repository: CareRepository,
) {
    suspend fun execute(): List<CareTask> = repository.careTasks()
}
