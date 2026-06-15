package com.barns.app.data.repository

import com.barns.app.domain.model.CareLog
import com.barns.app.domain.model.CareRepeatRule
import com.barns.app.domain.model.CareTask
import com.barns.app.domain.model.CareTaskStatus
import com.barns.app.domain.model.CareType
import com.barns.app.domain.repository.CareRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * In-memory, local-only care data. No persistence, no network.
 * Seed data is fake, non-sensitive, and image-null compatible.
 */
class MockCareRepository : CareRepository {
    private val lock = Any()
    private val tasks = mutableListOf(
        CareTask(
            id = "care-task-001",
            productItemId = "item-wall-green-001",
            title = "Inspect wall greenery",
            careType = CareType.INSPECTION,
            dueDate = Instant.now().plus(2, ChronoUnit.DAYS),
            repeatRule = CareRepeatRule.MONTHLY,
            status = CareTaskStatus.PENDING,
            reminderEnabled = false,
            imageUrl = null,
        ),
        CareTask(
            id = "care-task-002",
            productItemId = "item-interior-green-001",
            title = "Water desk planter",
            careType = CareType.WATERING,
            dueDate = Instant.now().plus(5, ChronoUnit.DAYS),
            repeatRule = CareRepeatRule.WEEKLY,
            status = CareTaskStatus.PENDING,
            reminderEnabled = false,
            imageUrl = null,
        ),
    )
    private val logs = listOf(
        CareLog(
            id = "care-log-001",
            productItemId = "item-wall-green-001",
            careTaskId = null,
            careType = CareType.CLEANING,
            performedAt = Instant.now().minus(7, ChronoUnit.DAYS),
            memo = null,
            imageUrl = null,
        ),
    )

    override suspend fun careTasks(): List<CareTask> =
        synchronized(lock) { tasks.toList() }

    override suspend fun careTask(id: String): CareTask? =
        synchronized(lock) { tasks.firstOrNull { it.id == id } }

    override suspend fun completeCareTask(id: String) {
        synchronized(lock) {
            val index = tasks.indexOfFirst { it.id == id }
            if (index >= 0) {
                tasks[index] = tasks[index].copy(status = CareTaskStatus.COMPLETED)
            }
        }
    }

    override suspend fun careLogs(productItemId: String?): List<CareLog> =
        synchronized(lock) {
            if (productItemId == null) logs else logs.filter { it.productItemId == productItemId }
        }
}
