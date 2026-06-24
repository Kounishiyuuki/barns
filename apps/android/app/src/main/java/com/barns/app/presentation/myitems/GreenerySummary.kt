package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Shared supporting summary for a registered greenery, used by the active
 * My Greenery list and the Archived Greenery list so both present the same
 * ownership / category / location rhythm. Presentation-only: it reads a
 * [ProductItemPresentation] and never touches data sources. The item name is
 * rendered separately as the list item's headline; the care-status line is
 * shown only where relevant (the active list).
 */
@Composable
fun GreenerySummary(
    display: ProductItemPresentation,
    showCareStatus: Boolean = false,
) {
    Column {
        Text(display.ownershipSummary)
        Text(
            text = "${display.categoryLabel} · ${display.locationLabel}",
            style = MaterialTheme.typography.bodySmall,
        )
        if (showCareStatus) {
            Text(
                text = display.careStatusLabel,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
