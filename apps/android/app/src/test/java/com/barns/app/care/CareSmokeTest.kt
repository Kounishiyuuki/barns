package com.barns.app.care

import com.barns.app.data.repository.MockCareRepository
import com.barns.app.domain.model.CareTaskStatus
import com.barns.app.domain.usecase.care.CompleteCareTaskUseCase
import com.barns.app.domain.usecase.care.GetCareTasksUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local-first care smoke tests: seeded tasks are listed and completing a task
 * flips its status in the in-memory store. No network, no persistence.
 */
class CareSmokeTest {

    @Test
    fun careTasksAreListed() = runTest {
        val repository = MockCareRepository()
        val getTasks = GetCareTasksUseCase(repository)

        assertTrue(getTasks.execute().isNotEmpty())
    }

    @Test
    fun completeCareTaskMarksItCompleted() = runTest {
        val repository = MockCareRepository()
        val getTasks = GetCareTasksUseCase(repository)
        val completeTask = CompleteCareTaskUseCase(repository)

        val pending = getTasks.execute().first { it.status == CareTaskStatus.PENDING }
        completeTask.execute(pending.id)
        val updated = getTasks.execute().first { it.id == pending.id }

        assertEquals(CareTaskStatus.COMPLETED, updated.status)
    }
}
