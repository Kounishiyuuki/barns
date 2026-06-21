package com.barns.app.domain.usecase.catalog

import com.barns.app.domain.model.GreeneryInfo
import com.barns.app.domain.repository.GreeneryInfoRepository

/** Returns official basic information for a greenery, if it exists. */
class GetGreeneryInfoUseCase(
    private val repository: GreeneryInfoRepository,
) {
    suspend fun execute(id: String): GreeneryInfo? = repository.greeneryInfo(id)
}
