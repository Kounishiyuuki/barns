package com.barns.app.domain.usecase.catalog

import com.barns.app.domain.model.CatalogItem
import com.barns.app.domain.repository.CatalogRepository

/** Returns a single official catalog item by id, if it exists. */
class GetCatalogItemDetailUseCase(
    private val repository: CatalogRepository,
) {
    suspend fun execute(id: String): CatalogItem? = repository.catalogItem(id)
}
