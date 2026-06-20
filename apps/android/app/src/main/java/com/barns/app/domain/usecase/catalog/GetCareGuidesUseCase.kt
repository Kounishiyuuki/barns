package com.barns.app.domain.usecase.catalog

import com.barns.app.domain.model.CareGuide
import com.barns.app.domain.repository.CareGuideRepository

/**
 * Returns official care guides. With no ids, returns all guides; with ids,
 * returns the matching guides (for a catalog item's `careGuideIds`).
 */
class GetCareGuidesUseCase(
    private val repository: CareGuideRepository,
) {
    suspend fun execute(): List<CareGuide> = repository.careGuides()

    suspend fun execute(ids: List<String>): List<CareGuide> = repository.careGuides(ids)
}
