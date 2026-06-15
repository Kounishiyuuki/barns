package com.barns.app.domain.model

import java.time.Instant

data class CareTask(
    val id: String,
    val productItemId: String,
    val title: String,
    val careType: CareType,
    val dueDate: Instant,
    val repeatRule: CareRepeatRule?,
    val status: CareTaskStatus,
    val reminderEnabled: Boolean,
    val imageUrl: String?,
)

enum class CareType { WATERING, CLEANING, PRUNING, INSPECTION, REPLACEMENT, OTHER }

enum class CareRepeatRule { DAILY, WEEKLY, MONTHLY, SEASONAL }

enum class CareTaskStatus { PENDING, COMPLETED, SKIPPED }
