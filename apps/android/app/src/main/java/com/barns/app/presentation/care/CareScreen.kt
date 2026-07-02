package com.barns.app.presentation.care

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.barns.app.app.DependencyContainer
import com.barns.app.domain.model.CareType
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val careDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault())

/**
 * Care host. Owns minimal local navigation between the list and detail.
 * Mirrors the iOS Care NavigationStack.
 */
@Composable
fun CareScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    var route by remember { mutableStateOf<CareRoute>(CareRoute.List) }

    when (val current = route) {
        CareRoute.List -> {
            val viewModel = remember(container) { container.makeCareViewModel() }
            CareListScreen(
                viewModel = viewModel,
                onTaskClick = { id -> route = CareRoute.Detail(id) },
                onBack = onBack,
            )
        }
        is CareRoute.Detail -> {
            val viewModel = remember(current.taskId) {
                container.makeCareTaskDetailViewModel(current.taskId)
            }
            CareTaskDetailScreen(viewModel = viewModel, onBack = { route = CareRoute.List })
        }
    }
}

@Composable
private fun CareListScreen(
    viewModel: CareViewModel,
    onTaskClick: (String) -> Unit,
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
        Text(
            text = "Care",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        when (val current = state) {
            CareViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CareViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is CareViewModel.State.Loaded -> {
                val content = current.content
                Text(text = "Upcoming", style = MaterialTheme.typography.titleMedium)
                if (content.upcoming.isEmpty()) {
                    Text(
                        text = "No upcoming care tasks. Care you plan for your My Greenery appears here.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    content.upcoming.forEach { task ->
                        ListItem(
                            modifier = Modifier.clickable(role = Role.Button) { onTaskClick(task.id) },
                            headlineContent = { Text(task.title) },
                            supportingContent = { Text("Due ${careDateFormatter.format(task.dueDate)}") },
                        )
                    }
                }
                Text(
                    text = "Recent care log",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (content.recentLogs.isEmpty()) {
                    Text(
                        text = "No care logged yet. Completed care is recorded here on this device.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    content.recentLogs.forEach { log ->
                        ListItem(
                            headlineContent = { Text(careTypeLabel(log.careType)) },
                            supportingContent = { Text(careDateFormatter.format(log.performedAt)) },
                        )
                    }
                }
                Text(
                    text = "Care tasks and logs are local records for your My Greenery. barns does " +
                        "not send reminders or notifications, and nothing is synced.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

/**
 * Short noun label for a logged care action, so recent care reads clearly
 * (e.g. "Watering") instead of showing only a date.
 */
private fun careTypeLabel(careType: CareType): String = when (careType) {
    CareType.WATERING -> "Watering"
    CareType.CLEANING -> "Cleaning"
    CareType.PRUNING -> "Pruning"
    CareType.INSPECTION -> "Inspection"
    CareType.REPLACEMENT -> "Replacement"
    CareType.OTHER -> "Care"
}

private sealed interface CareRoute {
    data object List : CareRoute
    data class Detail(val taskId: String) : CareRoute
}
