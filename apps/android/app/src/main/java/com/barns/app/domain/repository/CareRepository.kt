package com.barns.app.domain.repository

import com.barns.app.domain.model.CareLog
import com.barns.app.domain.model.CareTask

interface CareRepository {
    suspend fun careTasks(): List<CareTask>
    suspend fun careTask(id: String): CareTask?
    suspend fun completeCareTask(id: String)
    suspend fun careLogs(productItemId: String?): List<CareLog>
}
