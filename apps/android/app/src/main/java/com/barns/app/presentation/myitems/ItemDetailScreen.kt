package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        when (val current = state) {
            ItemDetailViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                ListItem(
                    headlineContent = { Text("Name") },
                    supportingContent = { Text(item.name) },
                )
                ListItem(
                    headlineContent = { Text("Category") },
                    supportingContent = { Text(item.categoryId) },
                )
                ListItem(
                    headlineContent = { Text("Installed place") },
                    supportingContent = { Text(item.locationLabel ?: "—") },
                )
                ListItem(
                    headlineContent = { Text("Next care") },
                    supportingContent = { Text("Not scheduled") },
                )
                ListItem(
                    headlineContent = { Text("Memo") },
                    supportingContent = { Text(item.notes ?: "—") },
                )
            }
        }
    }
}
