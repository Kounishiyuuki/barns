package com.barns.app.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Home screen. Source skeleton: renders the MVP summary state.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenMyItems: () -> Unit = {},
    onOpenCare: () -> Unit = {},
    onOpenPatterns: () -> Unit = {},
    onOpenSupport: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.load()
    }

    when (val current = state) {
        HomeViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeViewModel.State.Failed -> {
            Text(
                modifier = Modifier.padding(16.dp),
                text = current.message,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        is HomeViewModel.State.Loaded -> {
            val content = current.content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = content.greeting, style = MaterialTheme.typography.titleLarge)
                Text(text = content.summary.welcomeMessage, style = MaterialTheme.typography.bodyMedium)
                ListItem(
                    modifier = Modifier.clickable { onOpenMyItems() },
                    headlineContent = { Text("Registered items") },
                    supportingContent = { Text(content.summary.registeredItemCount.toString()) },
                )
                ListItem(
                    modifier = Modifier.clickable { onOpenCare() },
                    headlineContent = { Text("Next care") },
                    supportingContent = { Text(content.summary.nextCareLabel) },
                )
                ListItem(
                    modifier = Modifier.clickable { onOpenPatterns() },
                    headlineContent = { Text("Patterns") },
                    supportingContent = { Text(content.summary.patternsEntryLabel) },
                )
                ListItem(
                    modifier = Modifier.clickable { onOpenSupport() },
                    headlineContent = { Text("Support") },
                    supportingContent = { Text(content.summary.supportGuidance) },
                )
            }
        }
    }
}
