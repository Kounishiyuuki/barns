package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.repository.ProductItemRepository

/**
 * Returns a single registered product item by id, if present.
 */
class GetProductItemDetailUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(id: String): ProductItem? = repository.productItem(id)
}
