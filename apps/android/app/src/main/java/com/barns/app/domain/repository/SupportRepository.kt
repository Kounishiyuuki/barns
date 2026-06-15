package com.barns.app.domain.repository

import com.barns.app.domain.model.CareGuide
import com.barns.app.domain.model.Category
import com.barns.app.domain.model.CompanyInfo
import com.barns.app.domain.model.Notice

interface SupportRepository {
    suspend fun categories(): List<Category>
    suspend fun careGuides(): List<CareGuide>
    suspend fun notices(): List<Notice>
    suspend fun companyInfo(): CompanyInfo
}
