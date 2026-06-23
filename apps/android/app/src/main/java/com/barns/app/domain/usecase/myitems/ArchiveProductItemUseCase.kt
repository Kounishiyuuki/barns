package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.repository.ProductItemRepository
import java.time.Instant

/**
 * Archives a registered greenery locally by setting its status to ARCHIVED.
 * This is a soft action: the item stays in the local store, official content
 * links are preserved, and nothing is sent to a server.
 */
class ArchiveProductItemUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(item: ProductItem): ProductItem {
        val archived = item.copy(
            status = ProductItemStatus.ARCHIVED,
            updatedAt = Instant.now(),
        )
        repository.updateProductItem(archived)
        return archived
    }
}
