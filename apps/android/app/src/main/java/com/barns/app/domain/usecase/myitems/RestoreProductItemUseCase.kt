package com.barns.app.domain.usecase.myitems

import com.barns.app.domain.model.ProductItem
import com.barns.app.domain.model.ProductItemStatus
import com.barns.app.domain.repository.ProductItemRepository
import java.time.Instant

/**
 * Restores an archived greenery locally by setting its status back to ACTIVE.
 * This is the inverse of [ArchiveProductItemUseCase]: a soft action — the item
 * already lives in the local store (no hard delete, no re-create/re-insert) and
 * all official content links (categoryId, careGuideIds, imageUrl) and other
 * stable fields are preserved. Only status flips and updatedAt is refreshed.
 * Local-only: never sent to a server.
 */
class RestoreProductItemUseCase(
    private val repository: ProductItemRepository,
) {
    suspend fun execute(item: ProductItem): ProductItem {
        val restored = item.copy(
            status = ProductItemStatus.ACTIVE,
            updatedAt = Instant.now(),
        )
        repository.updateProductItem(restored)
        return restored
    }
}
