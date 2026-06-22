package com.barns.app.domain.repository

import com.barns.app.domain.model.ProductItem

interface ProductItemRepository {
    suspend fun productItems(): List<ProductItem>
    suspend fun productItem(id: String): ProductItem?
    suspend fun addProductItem(item: ProductItem)

    /** Replaces the locally stored item with the same id. Local-only; never
     * sent to a server. */
    suspend fun updateProductItem(item: ProductItem)
}
