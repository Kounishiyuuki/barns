package com.barns.app.data.repository

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.repository.ProductItemRepository

/**
 * In-memory, local-only product items. No persistence, no network.
 * Seed data is fake, non-sensitive, and image-null compatible.
 */
class MockProductItemRepository : ProductItemRepository {
    private val lock = Any()
    private val items = mutableListOf(
        ProductItem(
            id = "item-wall-green-001",
            categoryId = "cat-wall-green",
            name = "Lobby wall greenery",
            type = ProductItemType.INSTALLED,
            installedOrPurchasedAt = null,
            locationLabel = "Entrance wall",
            status = ProductItemStatus.ACTIVE,
            careGuideIds = emptyList(),
            notes = null,
            imageUrl = null,
            updatedAt = null,
        ),
        ProductItem(
            id = "item-interior-green-001",
            categoryId = "cat-interior-green",
            name = "Desk planter",
            type = ProductItemType.PURCHASED,
            installedOrPurchasedAt = null,
            locationLabel = "Meeting room",
            status = ProductItemStatus.ACTIVE,
            careGuideIds = emptyList(),
            notes = null,
            imageUrl = null,
            updatedAt = null,
        ),
    )

    override suspend fun productItems(): List<ProductItem> =
        synchronized(lock) { items.toList() }

    override suspend fun productItem(id: String): ProductItem? =
        synchronized(lock) { items.firstOrNull { it.id == id } }

    override suspend fun addProductItem(item: ProductItem) {
        synchronized(lock) { items.add(item) }
    }

    override suspend fun updateProductItem(item: ProductItem) {
        synchronized(lock) {
            val index = items.indexOfFirst { it.id == item.id }
            if (index >= 0) items[index] = item
        }
    }
}
