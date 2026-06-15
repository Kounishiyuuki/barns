package com.barns.app.domain.usecase.care

import com.barns.app.domain.model.CareTask
import com.barns.app.domain.repository.CareRepository

/**
 * Returns a single care task by id, if present.
 */
class GetCareTaskDetailUseCase(
    private val repository: CareRepository,
) {
    suspend fun execute(id: String): CareTask? = repository.careTask(id)
}
