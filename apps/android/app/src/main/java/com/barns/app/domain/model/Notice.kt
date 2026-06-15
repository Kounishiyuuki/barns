package com.barns.app.domain.model

import java.time.Instant

data class Notice(
    val id: String,
    val title: String,
    val body: String,
    val publishedAt: Instant,
    val priority: NoticePriority,
    val imageUrl: String?,
)

enum class NoticePriority { LOW, NORMAL, HIGH }
