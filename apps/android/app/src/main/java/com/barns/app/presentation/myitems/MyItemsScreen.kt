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
                onBack = onBack,
            )
        }
        is MyItemsRoute.Detail -> {
            val viewModel = remember(current.itemId) {
                container.makeItemDetailViewModel(current.itemId)
            }
            ItemDetailScreen(viewModel = viewModel, onBack = { route = MyItemsRoute.List })
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
            Button(onClick = onAddClick) { Text("Add item") }
        }
        Text(
            text = "My Items",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
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
                        text = "Your registered greenery will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    LazyColumn {
                        items(current.items) { item ->
                            ListItem(
                                modifier = Modifier.clickable { onItemClick(item.id) },
                                headlineContent = { Text(item.name) },
                                supportingContent = item.locationLabel?.let { { Text(it) } },
                            )
                        }
                    }
                }
            }
        }
    }
}

private sealed interface MyItemsRoute {
    data object List : MyItemsRoute
    data class Detail(val itemId: String) : MyItemsRoute
    data object Add : MyItemsRoute
}
