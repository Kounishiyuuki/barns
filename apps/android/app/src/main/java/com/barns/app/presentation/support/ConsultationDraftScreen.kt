package com.barns.app.presentation.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
 * Local-only consultation draft input. The draft is never sent to a server.
 */
@Composable
fun ConsultationDraftScreen(
    viewModel: ConsultationDraftViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        Text("Consultation draft", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = state.topic,
            onValueChange = viewModel::onTopicChange,
            label = { Text("Topic") },
            modifier = Modifier.fillMaxWidth(),
        )

        Text("Category", style = MaterialTheme.typography.titleMedium)
        viewModel.categories.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.category == category,
                    onClick = { viewModel.onCategoryChange(category) },
                )
                Text(category.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        Text("Urgency", style = MaterialTheme.typography.titleMedium)
        viewModel.urgencies.forEach { urgency ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.urgency == urgency,
                    onClick = { viewModel.onUrgencyChange(urgency) },
                )
                Text(urgency.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        OutlinedTextField(
            value = state.body,
            onValueChange = viewModel::onBodyChange,
            label = { Text("Details") },
            modifier = Modifier.fillMaxWidth(),
        )

        state.errorMessage?.let { message ->
            Text(text = message)
        }
        if (state.savedAt != null) {
            Text(text = "Saved locally.", style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = viewModel::save,
            enabled = state.canSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save draft")
        }
        Text(
            text = "Saved locally only. Not sent to any server.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
