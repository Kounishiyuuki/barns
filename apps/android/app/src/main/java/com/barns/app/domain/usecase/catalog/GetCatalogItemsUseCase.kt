package com.barns.app.domain.usecase.catalog

import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.repository.CatalogRepository

/** Returns the official, read-only catalog items. */
class GetCatalogItemsUseCase(
    private val repository: CatalogRepository,
) {
    suspend fun execute(): List<CatalogItem> = repository.catalogItems()
}
