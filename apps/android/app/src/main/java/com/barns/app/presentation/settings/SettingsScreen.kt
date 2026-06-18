package com.barns.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barns.app.app.DependencyContainer

/**
 * Minimal, local-only Settings screen for the MVP. Static informational
 * content only: app status, support guidance, privacy note, and a
 * development note. No external links, no tel:/mailto:, no real contact data.
 */
@Composable
fun SettingsScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    val viewModel = remember(container) { container.makeSettingsViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        viewModel.sections.forEach { section ->
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            section.items.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text(item.detail) },
                )
            }
        }
    }
}
