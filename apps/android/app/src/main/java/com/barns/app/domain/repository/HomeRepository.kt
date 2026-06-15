package com.barns.app.domain.repository

import com.barns.app.domain.model.HomeSummary

interface HomeRepository {
    suspend fun homeSummary(): HomeSummary
}
