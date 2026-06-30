package com.barns.app.common

import com.barns.app.data.repository.MockCatalogRepository
import com.barns.app.data.repository.MockProductItemRepository
import com.barns.app.presentation.common.localMockDrawableRes
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests for the local mock image reference resolver. Verifies pure
 * `mock://…` reference mapping to bundled drawables. Mock/demo, local-only;
 * no network is involved and only mapped local references resolve.
 */
class LocalMockImageSmokeTest {

    @Test
    fun catalogMockReferenceResolvesToDrawable() {
        assertNotNull(
            localMockDrawableRes("mock://catalog/catalog-office-vertical-green-wall-01"),
        )
    }

    @Test
    fun myGreeneryMockReferenceResolvesToDrawable() {
        assertNotNull(
            localMockDrawableRes("mock://my-greenery/my-greenery-entryway-green-wall-01"),
        )
    }

    @Test
    fun nullReferenceResolvesToNull() {
        assertNull(localMockDrawableRes(null))
    }

    @Test
    fun remoteOrUnknownReferenceResolvesToNull() {
        // Guardrail: only local `mock://` references are honored; remote URLs
        // never resolve to a drawable (no network image loading).
        assertNull(localMockDrawableRes("https://example.com/x.jpg"))
        assertNull(localMockDrawableRes("http://example.com/x.jpg"))
        assertNull(localMockDrawableRes("mock://catalog"))
        assertNull(localMockDrawableRes("mock://catalog/unknown-asset"))
    }

    @Test
    fun wrongCategoryOrCrossCategoryReferenceResolvesToNull() {
        assertNull(
            localMockDrawableRes("mock://wrong-category/catalog-office-vertical-green-wall-01"),
        )
        assertNull(
            localMockDrawableRes("mock://catalog/my-greenery-entryway-green-wall-01"),
        )
    }

    @Test
    fun seededProductItemsKeepStableCountAndLocalReferences() = runTest {
        val items = MockProductItemRepository().productItems()
        assertEquals(2, items.size)
        items.forEach { item ->
            item.imageUrl?.let { assertTrue(it.startsWith("mock://")) }
        }
    }

    @Test
    fun seededCatalogItemsUseLocalMockOrNullReferences() = runTest {
        val items = MockCatalogRepository().catalogItems()
        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            item.imageUrl?.let { assertTrue(it.startsWith("mock://")) }
        }
    }
}
