package com.barns.app.presentation.myitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barns.app.app.DependencyContainer
import com.barns.app.domain.model.ProductItem
import com.barns.app.presentation.support.ConsultationDraftScreen

/**
 * My Items host. Owns minimal local navigation between the list, detail,
 * and add screens. Mirrors the iOS My Items NavigationStack.
 */
@Composable
fun MyItemsScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    var route by remember { mutableStateOf<MyItemsRoute>(MyItemsRoute.List) }

    when (val current = route) {
        MyItemsRoute.List -> {
            val viewModel = remember(container) { container.makeMyItemsViewModel() }
            MyItemsListScreen(
                viewModel = viewModel,
                onItemClick = { id -> route = MyItemsRoute.Detail(id) },
                onAddClick = { route = MyItemsRoute.Add },
                onArchivedClick = { route = MyItemsRoute.Archived },
                onBack = onBack,
            )
        }
        MyItemsRoute.Archived -> {
            val viewModel = remember(container) { container.makeArchivedGreeneryViewModel() }
            ArchivedGreeneryScreen(
                viewModel = viewModel,
                onBack = { route = MyItemsRoute.List },
            )
        }
        is MyItemsRoute.Detail -> {
            val viewModel = remember(current.itemId) {
                container.makeItemDetailViewModel(current.itemId)
            }
            ItemDetailScreen(
                viewModel = viewModel,
                onBack = { route = MyItemsRoute.List },
                onPrepareConsultation = { item -> route = MyItemsRoute.Consultation(item) },
                onEdit = { item -> route = MyItemsRoute.Edit(item) },
                onArchived = { route = MyItemsRoute.List },
            )
        }
        is MyItemsRoute.Consultation -> {
            val item = current.item
            val viewModel = remember(item.id) { container.makeConsultationDraftViewModel(item) }
            ConsultationDraftScreen(
                viewModel = viewModel,
                onBack = { route = MyItemsRoute.Detail(item.id) },
            )
        }
        is MyItemsRoute.Edit -> {
            // Local-only edit of a registered greenery. Returns to the detail
            // on save or cancel; no write happens until the user taps Save.
            val item = current.item
            val viewModel = remember(item.id) { container.makeEditGreeneryViewModel(item) }
            EditGreeneryScreen(
                viewModel = viewModel,
                onSaved = { route = MyItemsRoute.Detail(item.id) },
                onCancel = { route = MyItemsRoute.Detail(item.id) },
            )
        }
        MyItemsRoute.Add -> {
            val viewModel = remember(container) { container.makeAddItemViewModel() }
            AddItemScreen(
                viewModel = viewModel,
                onSaved = { route = MyItemsRoute.List },
                onCancel = { route = MyItemsRoute.List },
            )
        }
    }
}

@Composable
private fun MyItemsListScreen(
    viewModel: MyItemsViewModel,
    onItemClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onArchivedClick: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack) { Text("Back") }
            Button(onClick = onAddClick) { Text("Register Greenery") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Greenery",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            // Low-emphasis entry to the archived greenery list. Archived items
            // stay customer-owned local data and can be restored.
            TextButton(onClick = onArchivedClick) { Text("Archived") }
        }
        when (val current = state) {
            MyItemsViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MyItemsViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is MyItemsViewModel.State.Loaded -> {
                if (current.items.isEmpty()) {
                    Text(
                        text = "Register your greenery",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        text = "Add the wall greening and interior green you own or had " +
                            "installed to keep their care and support in one place.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    LazyColumn {
                        items(current.items) { item ->
                            val display = ProductItemPresentation.from(item)
                            ListItem(
                                modifier = Modifier.clickable { onItemClick(item.id) },
                                headlineContent = { Text(display.name) },
                                supportingContent = {
                                    GreenerySummary(display = display, showCareStatus = true)
                                },
                            )
                        }
                    }
                    Text(
                        text = "Your greenery registry stays on this device.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

private sealed interface MyItemsRoute {
    data object List : MyItemsRoute
    data class Detail(val itemId: String) : MyItemsRoute
    data class Consultation(val item: ProductItem) : MyItemsRoute
    data class Edit(val item: ProductItem) : MyItemsRoute
    data object Add : MyItemsRoute
    data object Archived : MyItemsRoute
}
