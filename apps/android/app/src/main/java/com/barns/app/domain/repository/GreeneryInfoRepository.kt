package com.barns.app.domain.repository

import com.barns.app.domain.model.GreeneryInfo

/**
 * Official, read-only greenery basic information. Mock now; a real read-only
 * API may back this later without changing use cases or screens.
 */
interface GreeneryInfoRepository {
    suspend fun greeneryInfo(id: String): GreeneryInfo?
}
