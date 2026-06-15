package com.barns.app.domain.usecase.home

import com.barns.app.domain.model.HomeSummary
import com.barns.app.domain.repository.HomeRepository

/**
 * Provides the lightweight home summary.
 */
class GetHomeSummaryUseCase(
    private val homeRepository: HomeRepository,
) {
    suspend fun execute(): HomeSummary = homeRepository.homeSummary()
}
