package com.barns.app.domain.repository

import com.barns.app.domain.model.CareGuide

/**
 * Official, read-only care/growing guidance. Mock now; a real read-only API
 * may back this later without changing use cases or screens.
 */
interface CareGuideRepository {
    suspend fun careGuides(): List<CareGuide>
    suspend fun careGuide(id: String): CareGuide?
    suspend fun careGuides(ids: List<String>): List<CareGuide>
}
