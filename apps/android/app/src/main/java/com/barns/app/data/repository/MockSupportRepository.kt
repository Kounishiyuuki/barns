package com.barns.app.data.repository

import com.barns.app.domain.model.CareGuide
import com.barns.app.domain.model.Category
import com.barns.app.domain.model.CompanyInfo
import com.barns.app.domain.model.Notice
import com.barns.app.domain.repository.SupportRepository

/**
 * In-memory, local-only support content. No persistence, no network.
 * Company info is fake and non-sensitive; phone/address are null.
 * Server-served content (categories, care guides, notices) is out of
 * scope for this flow and returned empty for now.
 */
class MockSupportRepository : SupportRepository {
    override suspend fun categories(): List<Category> = emptyList()

    override suspend fun careGuides(): List<CareGuide> = emptyList()

    override suspend fun notices(): List<Notice> = emptyList()

    override suspend fun companyInfo(): CompanyInfo =
        CompanyInfo(
            id = "company-info-default",
            displayName = "barns support",
            description = "After-purchase support for wall greenery and interior green.",
            inquiryPolicy = "For the MVP, external inquiries are guided to phone consultation.",
            phoneLabel = "Call for consultation",
            phoneNumber = null,
            address = null,
            businessHoursNote = "Business hours will be set after company confirmation.",
            imageUrl = null,
        )
}
