package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.model.ProductItemType
import com.barns.app.domain.repository.ProductItemRepository
import java.time.Instant

/**
 * Updates an existing local product item from edited customer-owned fields.
 * Local-only: the item is never sent to a server. Official content links
 * (categoryId, careGuideIds, imageUrl) and creation metadata are preserved;
 * only customer-editable fields change.
 */
class UpdateProductItemUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(
        original: ProductItem,
        name: String,
        type: ProductItemType,
        locationLabel: String?,
        notes: String?,
        status: ProductItemStatus,
    ): ProductItem {
        val updated = original.copy(
            name = name,
            type = type,
            locationLabel = locationLabel,
            notes = notes,
            status = status,
            updatedAt = Instant.now(),
        )
        repository.updateProductItem(updated)
        return updated
    }
}
