package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.repository.ProductItemRepository
import java.time.Instant
import java.util.UUID

/**
 * Adds a new local product item built from user input.
 * Local-only: the item is never sent to a server.
 */
class AddProductItemUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(
        name: String,
        categoryId: String,
        type: ProductItemType = ProductItemType.PURCHASED,
        locationLabel: String?,
        notes: String?,
    ): ProductItem {
        val item = ProductItem(
            id = UUID.randomUUID().toString(),
            categoryId = categoryId,
            name = name,
            type = type,
            installedOrPurchasedAt = null,
            locationLabel = locationLabel,
            status = ProductItemStatus.ACTIVE,
            careGuideIds = emptyList(),
            notes = notes,
            imageUrl = null,
            updatedAt = Instant.now(),
        )
        repository.addProductItem(item)
        return item
    }
}
