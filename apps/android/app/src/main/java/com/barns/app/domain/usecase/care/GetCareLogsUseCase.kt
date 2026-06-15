package com.barns.app.domain.usecase.care

import com.barns.app.domain.model.CareLog
import com.barns.app.domain.repository.CareRepository

/**
 * Returns local care logs, optionally filtered by product item.
 */
class GetCareLogsUseCase(
    private val repository: CareRepository,
) {
    suspend fun execute(productItemId: String? = null): List<CareLog> =
        repository.careLogs(productItemId)
}
