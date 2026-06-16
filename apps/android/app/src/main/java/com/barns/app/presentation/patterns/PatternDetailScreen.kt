package com.barns.app.presentation.patterns

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
fun PatternDetailScreen(
    viewModel: PatternDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        when (val current = state) {
            PatternDetailViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            PatternDetailViewModel.State.NotFound -> {
                Text(text = "Pattern not found", style = MaterialTheme.typography.bodyMedium)
            }
            is PatternDetailViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is PatternDetailViewModel.State.Loaded -> {
                val pattern = current.pattern
                ListItem(
                    headlineContent = { Text("Title") },
                    supportingContent = { Text(pattern.name) },
                )
                ListItem(
                    headlineContent = { Text("Recommended place") },
                    supportingContent = { Text(pattern.recommendedSpace) },
                )
                ListItem(
                    headlineContent = { Text("Difficulty") },
                    supportingContent = { Text(pattern.maintenanceLevel.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
                ListItem(
                    headlineContent = { Text("Description") },
                    supportingContent = { Text(pattern.description) },
                )
                ListItem(
                    headlineContent = { Text("Care guide") },
                    supportingContent = { Text("See the care guides for upkeep details.") },
                )
            }
        }
    }
}
