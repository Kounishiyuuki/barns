package com.barns.app.presentation.myitems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barns.app.domain.model.ProductItemType

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Register Greenery",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Register the wall greening or interior green you own or had installed.",
            style = MaterialTheme.typography.bodyMedium,
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Greenery name") },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(text = "Registration type", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = state.type == ProductItemType.INSTALLED,
                onClick = { viewModel.onTypeChange(ProductItemType.INSTALLED) },
                label = { Text("Installed greenery") },
            )
            FilterChip(
                selected = state.type == ProductItemType.PURCHASED,
                onClick = { viewModel.onTypeChange(ProductItemType.PURCHASED) },
                label = { Text("Owned greenery") },
            )
        }

        OutlinedTextField(
            value = state.locationLabel,
            onValueChange = viewModel::onLocationChange,
            label = { Text("Installation or placement") },
            supportingText = { Text("For example, the room or wall where this greenery is placed.") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notes for your own reference") },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Your registry is kept locally on this device in the current MVP. " +
                "Care and support guidance stay in one place.",
            style = MaterialTheme.typography.bodySmall,
        )
        state.errorMessage?.let { message ->
            Text(text = message)
        }
        Button(
            onClick = { viewModel.save(onSaved) },
            enabled = state.canSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Register")
        }
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}
