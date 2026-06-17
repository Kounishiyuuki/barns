package com.barns.app.myitems

import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.domain.usecase.myitems.AddProductItemUseCase
import com.barns.app.domain.usecase.myitems.GetProductItemsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local-first My Items smoke tests: seeded items are listed and a new item is
 * appended to the in-memory store. No network, no persistence.
 */
class MyItemsSmokeTest {

    @Test
    fun seededItemsAreListed() = runTest {
        val repository = MockProductItemRepository()
        val getItems = GetProductItemsUseCase(repository)

        val items = getItems.execute()

        assertEquals(2, items.size)
    }

    @Test
    fun addItemAppendsLocally() = runTest {
        val repository = MockProductItemRepository()
        val addItem = AddProductItemUseCase(repository)
        val getItems = GetProductItemsUseCase(repository)

        val before = getItems.execute().size
        val created = addItem.execute(
            name = "New planter",
            categoryId = "cat-interior-green",
            locationLabel = null,
            notes = null,
        )
        val after = getItems.execute()

        assertEquals(before + 1, after.size)
        assertTrue(after.any { it.id == created.id })
        // Image fields stay optional for the MVP.
        assertNull(created.imageUrl)
    }
}
