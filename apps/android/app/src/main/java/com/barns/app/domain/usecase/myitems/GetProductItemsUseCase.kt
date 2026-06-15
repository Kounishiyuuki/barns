package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.repository.ProductItemRepository

/**
 * Returns the user's locally registered product items.
 */
class GetProductItemsUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(): List<ProductItem> = repository.productItems()
}
