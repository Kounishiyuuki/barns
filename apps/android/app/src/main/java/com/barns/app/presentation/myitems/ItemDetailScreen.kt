package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
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

                SectionHeader("Support")
                Text(
                    text = "Need a hand with this greenery? Phone consultation guidance " +
                        "is available from the Support screen.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                item.notes?.takeIf { it.isNotEmpty() }?.let { notes ->
                    SectionHeader("Memo")
                    Text(text = notes, style = MaterialTheme.typography.bodyMedium)
                }

                Text(
                    text = "This registry is kept locally on your device in the current MVP.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
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
