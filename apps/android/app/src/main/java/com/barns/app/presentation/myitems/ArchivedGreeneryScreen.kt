package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barns.app.domain.model.ProductItem

/**
 * Local-only Archived Greenery list. Shows the customer's archived greenery and
 * offers a single restore action per item (confirmed via a dialog). Read-only
 * otherwise: no hard delete, no bulk actions, no sort/filter UI.
 */
@Composable
fun ArchivedGreeneryScreen(
    viewModel: ArchivedGreeneryViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var itemPendingRestore by remember { mutableStateOf<ProductItem?>(null) }

    LaunchedEffect(viewModel) { viewModel.load() }

    itemPendingRestore?.let { item ->
        AlertDialog(
            onDismissRequest = { itemPendingRestore = null },
            title = { Text("Restore to My Greenery") },
            text = {
                Text("${item.name} will return to your active My Greenery list on this device.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.restore(item)
                    itemPendingRestore = null
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { itemPendingRestore = null }) { Text("Cancel") }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(
            text = "Archived Greenery",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        when (val current = state) {
            ArchivedGreeneryViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ArchivedGreeneryViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is ArchivedGreeneryViewModel.State.Loaded -> {
                if (current.items.isEmpty()) {
                    Text(
                        text = "No archived greenery",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        text = "Greenery you archive is kept here on this device. You can " +
                            "restore it to My Greenery anytime.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    LazyColumn {
                        items(current.items) { item ->
                            val display = ProductItemPresentation.from(item)
                            ListItem(
                                headlineContent = { Text(display.name) },
                                supportingContent = {
                                    GreenerySummary(display = display)
                                },
                                trailingContent = {
                                    TextButton(onClick = { itemPendingRestore = item }) {
                                        Text("Restore")
                                    }
                                },
                            )
                        }
                    }
                    Text(
                        text = "Archived greenery stays on this device and is never deleted.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}
