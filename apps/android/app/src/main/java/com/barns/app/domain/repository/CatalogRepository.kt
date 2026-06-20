package com.barns.app.domain.repository

import com.barns.app.domain.model.CatalogItem

/**
 * Official, read-only catalog content. Mock now; a real read-only API may
 * back this later without changing use cases or screens.
 */
interface CatalogRepository {
    suspend fun catalogItems(): List<CatalogItem>
    suspend fun catalogItem(id: String): CatalogItem?
}
