package com.barns.app.presentation.support

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
 * UI-only guidance screen. No real phone number; does not place a call.
 */
@Composable
fun PhoneInquiryScreen(
    viewModel: PhoneInquiryViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        when (val current = state) {
            PhoneInquiryViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PhoneInquiryViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is PhoneInquiryViewModel.State.Loaded -> {
                val info = current.info
                Text(
                    text = "Phone consultation",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Text(text = info.inquiryPolicy, style = MaterialTheme.typography.bodyMedium)
                ListItem(
                    headlineContent = { Text(info.phoneLabel) },
                    supportingContent = { Text(info.phoneNumber ?: "To be announced") },
                )
                info.businessHoursNote?.let { hours ->
                    Text(text = hours, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = "Calling from the app is not enabled yet.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
