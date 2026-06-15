package com.barns.app.domain.model

import java.time.Instant

data class CareLog(
    val id: String,
    val productItemId: String,
    val careTaskId: String?,
    val careType: CareType,
    val performedAt: Instant,
    val memo: String?,
    val imageUrl: String?,
)
