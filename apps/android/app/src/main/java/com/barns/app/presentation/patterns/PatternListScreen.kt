package com.barns.app.presentation.patterns

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.barns.app.app.DependencyContainer

/**
 * Patterns host. Owns minimal local navigation between the list and detail.
 */
@Composable
fun PatternListScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    var route by remember { mutableStateOf<PatternRoute>(PatternRoute.List) }

    when (val current = route) {
        PatternRoute.List -> {
            val viewModel = remember(container) { container.makePatternListViewModel() }
            PatternListContent(
                viewModel = viewModel,
                onPatternClick = { id -> route = PatternRoute.Detail(id) },
                onBack = onBack,
            )
        }
        is PatternRoute.Detail -> {
            val viewModel = remember(current.patternId) {
                container.makePatternDetailViewModel(current.patternId)
            }
            PatternDetailScreen(viewModel = viewModel, onBack = { route = PatternRoute.List })
        }
    }
}

@Composable
private fun PatternListContent(
    viewModel: PatternListViewModel,
    onPatternClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        Text(
            text = "Patterns",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        when (val current = state) {
            PatternListViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PatternListViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is PatternListViewModel.State.Loaded -> {
                LazyColumn {
                    items(current.patterns) { pattern ->
                        ListItem(
                            modifier = Modifier.clickable(role = Role.Button) { onPatternClick(pattern.id) },
                            headlineContent = { Text(pattern.name) },
                            supportingContent = { Text(pattern.recommendedSpace) },
                        )
                    }
                }
            }
        }
    }
}

private sealed interface PatternRoute {
    data object List : PatternRoute
    data class Detail(val patternId: String) : PatternRoute
}
