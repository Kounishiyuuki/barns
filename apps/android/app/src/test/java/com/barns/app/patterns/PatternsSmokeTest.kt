package com.barns.app.patterns

import com.barns.app.data.repository.MockPatternRepository
import com.barns.app.domain.usecase.patterns.GetPatternsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Read-only patterns smoke test: local seed patterns load and remain
 * image-null compatible. No network.
 */
class PatternsSmokeTest {

    @Test
    fun patternsLoad() = runTest {
        val repository = MockPatternRepository()
        val getPatterns = GetPatternsUseCase(repository)

        val patterns = getPatterns.execute()

        assertTrue(patterns.isNotEmpty())
        patterns.forEach { assertNull(it.imageUrl) }
    }
}
