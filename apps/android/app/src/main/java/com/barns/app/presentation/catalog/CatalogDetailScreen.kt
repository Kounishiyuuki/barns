package com.barns.app.presentation.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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

/**
 * Concise, official read-only catalog detail. Reference content only — no
 * price, stock, cart, order, or registration actions in this screen.
 */
@Composable
fun CatalogDetailScreen(
    viewModel: CatalogDetailViewModel,
    onBack: () -> Unit,
    onRegister: (com.barns.app.presentation.myitems.RegisterGreeneryPrefill) -> Unit = {},
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
            CatalogDetailViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            CatalogDetailViewModel.State.NotFound -> {
                Text(text = "Item not found", style = MaterialTheme.typography.bodyMedium)
            }
            is CatalogDetailViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is CatalogDetailViewModel.State.Loaded -> {
                val detail = current.content

                Text(
                    text = "Catalog item",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
                ListItem(
                    headlineContent = { Text("Name") },
                    supportingContent = { Text(detail.name) },
                )
                ListItem(
                    headlineContent = { Text("Kind") },
                    supportingContent = { Text(detail.kindLabel) },
                )
                Text(
                    text = detail.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = "Official read-only reference content.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )

                if (detail.hasBasicInformation) {
                    SectionHeader("Basic information")
                    detail.overview?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                    detail.lightPreference?.let {
                        ListItem(
                            headlineContent = { Text("Light") },
                            supportingContent = { Text(it) },
                        )
                    }
                    detail.wateringOverview?.let {
                        ListItem(
                            headlineContent = { Text("Watering") },
                            supportingContent = { Text(it) },
                        )
                    }
                }

                if (detail.hasCareGuides) {
                    SectionHeader("Care guide")
                    detail.careGuides.forEach { guide ->
                        ListItem(
                            headlineContent = { Text(guide.title) },
                            supportingContent = { Text(guide.summary) },
                        )
                    }
                }

                SectionHeader("My Greenery")
                Button(
                    onClick = { onRegister(detail.registerPrefill) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Register to My Greenery")
                }
                Text(
                    text = "Already have this greenery? Add it to your local My Greenery registry. " +
                        "This creates a local entry only — nothing is ordered, reserved, or submitted.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
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
