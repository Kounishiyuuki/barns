package com.barns.app.domain.usecase.support

import com.barns.app.domain.model.CompanyInfo
import com.barns.app.domain.repository.SupportRepository

/**
 * Returns company support info for the Support screen.
 */
class GetSupportInfoUseCase(
    private val repository: SupportRepository,
) {
    suspend fun execute(): CompanyInfo = repository.companyInfo()
}
