package com.barns.app.data.repository

import com.barns.app.domain.model.HomeSummary
import com.barns.app.domain.repository.HomeRepository

/**
 * Static, non-sensitive home summary for the MVP source skeleton.
 * Counts and labels are placeholders; no customer-side private data is used.
 */
class MockHomeRepository : HomeRepository {
    override suspend fun homeSummary(): HomeSummary =
        HomeSummary(
            welcomeMessage = "Your after-sales care starts here.",
            registeredItemCount = 2,
            nextCareLabel = "No upcoming care yet",
            supportGuidance = "Need help? Phone consultation guidance will appear here.",
            patternsEntryLabel = "Browse wall greenery patterns",
        )
}
