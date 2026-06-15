package com.barns.app.presentation.care

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.barns.app.domain.model.CareTaskStatus
import com.barns.app.domain.model.CareType
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val careDetailDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault())

@Composable
fun CareTaskDetailScreen(
    viewModel: CareTaskDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val isCompleting by viewModel.isCompleting.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        when (val current = state) {
            CareTaskDetailViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            CareTaskDetailViewModel.State.NotFound -> {
                Text(text = "Task not found", style = MaterialTheme.typography.bodyMedium)
            }
            is CareTaskDetailViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is CareTaskDetailViewModel.State.Loaded -> {
                val task = current.task
                ListItem(
                    headlineContent = { Text("Title") },
                    supportingContent = { Text(task.title) },
                )
                ListItem(
                    headlineContent = { Text("Related item") },
                    supportingContent = { Text(task.productItemId) },
                )
                ListItem(
                    headlineContent = { Text("Scheduled") },
                    supportingContent = { Text(careDetailDateFormatter.format(task.dueDate)) },
                )
                ListItem(
                    headlineContent = { Text("Description") },
                    supportingContent = { Text(descriptionFor(task.careType)) },
                )
                ListItem(
                    headlineContent = { Text("Status") },
                    supportingContent = { Text(task.status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
                val isCompleted = task.status == CareTaskStatus.COMPLETED
                Button(
                    onClick = { viewModel.complete() },
                    enabled = !isCompleted && !isCompleting,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    Text(if (isCompleted) "Completed" else "Mark as completed")
                }
            }
        }
    }
}

private fun descriptionFor(careType: CareType): String =
    when (careType) {
        CareType.WATERING -> "Water this item."
        CareType.CLEANING -> "Clean this item."
        CareType.PRUNING -> "Prune this item."
        CareType.INSPECTION -> "Inspect this item's condition."
        CareType.REPLACEMENT -> "Replace this item or its parts."
        CareType.OTHER -> "General care."
    }
