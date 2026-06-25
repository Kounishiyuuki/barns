package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.barns.app.domain.model.ProductItemStatus

@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit,
    onPrepareConsultation: (ProductItem) -> Unit = {},
    onEdit: (ProductItem) -> Unit = {},
    onArchived: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val official by viewModel.officialContent.collectAsState()
    var showArchiveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) { viewModel.load() }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Archive this greenery?") },
            text = {
                Text(
                    "It will be removed from your active My Greenery list on this device. " +
                        "Nothing is deleted permanently.",
                )
            },
            confirmButton = {
                // Cautious, destructive-like emphasis. Archiving is still a soft
                // status update (active -> archived), never a hard delete.
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archive(onArchived = onArchived)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text("Archive") }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("Back") }
            (state as? ItemDetailViewModel.State.Loaded)?.let { loaded ->
                TextButton(onClick = { onEdit(loaded.item) }) { Text("Edit") }
            }
        }
        when (val current = state) {
            ItemDetailViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            ItemDetailViewModel.State.NotFound -> {
                Text(text = "Item not found", style = MaterialTheme.typography.bodyMedium)
            }
            is ItemDetailViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is ItemDetailViewModel.State.Loaded -> {
                val item = current.item
                val display = ProductItemPresentation.from(item)

                Text(
                    text = "Registered greenery",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )

                SectionHeader("Overview")
                ListItem(
                    headlineContent = { Text("Name") },
                    supportingContent = { Text(display.name) },
                )
                ListItem(
                    headlineContent = { Text("Type") },
                    supportingContent = { Text(display.typeLabel) },
                )
                ListItem(
                    headlineContent = { Text("Category") },
                    supportingContent = { Text(display.categoryLabel) },
                )
                ListItem(
                    headlineContent = { Text("Installed place") },
                    supportingContent = { Text(display.locationLabel) },
                )
                ListItem(
                    headlineContent = { Text("Status") },
                    supportingContent = { Text(display.statusLabel) },
                )

                SectionHeader("Care")
                ListItem(
                    headlineContent = { Text("Care status") },
                    supportingContent = { Text(display.careStatusLabel) },
                )
                Text(
                    text = display.nextActionHint,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )

                official?.let { OfficialContentSections(it) }

                SectionHeader("Support")
                Button(
                    onClick = { onPrepareConsultation(item) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Prepare consultation note")
                }
                Text(
                    text = "Gather details about this greenery before contacting support. " +
                        "Phone consultation guidance is available from the Support screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )

                item.notes?.takeIf { it.isNotEmpty() }?.let { notes ->
                    SectionHeader("Memo")
                    Text(text = notes, style = MaterialTheme.typography.bodyMedium)
                }

                Text(
                    text = "Registered locally as your own greenery, kept on this device. Official " +
                        "catalog and care content is read-only reference and is not changed here.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )

                if (item.status == ProductItemStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = { showArchiveDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        Text("Archive greenery")
                    }
                    Text(
                        text = "Archiving removes it from your active list. It is not deleted.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
    )
}

/**
 * Official, read-only reference sections shown as supporting after-care
 * information. Not personalized and not submitted anywhere.
 */
@Composable
private fun OfficialContentSections(official: ItemOfficialContent) {
    if (official.hasBasicInformation) {
        SectionHeader("Basic information")
        official.overview?.let { overview ->
            Text(text = overview, style = MaterialTheme.typography.bodyMedium)
        }
        official.lightPreference?.let { light ->
            ListItem(
                headlineContent = { Text("Light") },
                supportingContent = { Text(light) },
            )
        }
        official.wateringOverview?.let { watering ->
            ListItem(
                headlineContent = { Text("Watering") },
                supportingContent = { Text(watering) },
            )
        }
        Text(
            text = "Official reference information.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    if (official.hasCareGuides) {
        SectionHeader("Care guide")
        official.careGuides.forEach { guide ->
            ListItem(
                headlineContent = { Text(guide.title) },
                supportingContent = { Text(guide.summary) },
            )
        }
    }
}
