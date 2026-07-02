package com.barns.app.presentation.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.barns.app.app.DependencyContainer
import com.barns.app.presentation.common.EmptyState
import com.barns.app.presentation.common.LocalMockImage
import com.barns.app.presentation.myitems.AddItemScreen

/**
 * Supporting Catalog host. Owns minimal local navigation between the official
 * read-only catalog list and detail. Reference content only — no price, stock,
 * cart, or order actions. Mirrors the iOS Catalog (PR #39).
 */
@Composable
fun CatalogScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    var route by remember { mutableStateOf<CatalogRoute>(CatalogRoute.List) }

    when (val current = route) {
        CatalogRoute.List -> {
            val viewModel = remember(container) { container.makeCatalogListViewModel() }
            CatalogListContent(
                viewModel = viewModel,
                onItemClick = { id -> route = CatalogRoute.Detail(id) },
                onBack = onBack,
            )
        }
        is CatalogRoute.Detail -> {
            val viewModel = remember(current.itemId) {
                container.makeCatalogDetailViewModel(current.itemId)
            }
            CatalogDetailScreen(
                viewModel = viewModel,
                onBack = { route = CatalogRoute.List },
                onRegister = { prefill -> route = CatalogRoute.Register(current.itemId, prefill) },
            )
        }
        is CatalogRoute.Register -> {
            // Local-only registration prefilled from the catalog item. Returns
            // to the originating detail on save or cancel. No write happens
            // until the user explicitly saves in the Register Greenery flow.
            val viewModel = remember(current) { container.makeAddItemViewModel(current.prefill) }
            AddItemScreen(
                viewModel = viewModel,
                onSaved = { route = CatalogRoute.Detail(current.itemId) },
                onCancel = { route = CatalogRoute.Detail(current.itemId) },
            )
        }
    }
}

@Composable
private fun CatalogListContent(
    viewModel: CatalogListViewModel,
    onItemClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        Text(
            text = "Official catalog",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        when (val current = state) {
            CatalogListViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatalogListViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is CatalogListViewModel.State.Loaded -> {
                if (current.items.isEmpty()) {
                    EmptyState(
                        title = "No catalog items",
                        message = "Official reference greenery will appear here. This is " +
                            "read-only reference content.",
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(current.items) { item ->
                            ListItem(
                                modifier = Modifier.clickable(role = Role.Button) { onItemClick(item.id) },
                                leadingContent = {
                                    LocalMockImage(
                                        reference = item.imageReference,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                    )
                                },
                                headlineContent = { Text(item.name) },
                                supportingContent = {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = item.kindLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                        Text(item.summary)
                                    }
                                },
                            )
                        }
                    }
                    Text(
                        text = "Official read-only reference content. Browse only — no ordering in the app.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

private sealed interface CatalogRoute {
    data object List : CatalogRoute
    data class Detail(val itemId: String) : CatalogRoute
    data class Register(
        val itemId: String,
        val prefill: com.barns.app.presentation.myitems.RegisterGreeneryPrefill,
    ) : CatalogRoute
}
