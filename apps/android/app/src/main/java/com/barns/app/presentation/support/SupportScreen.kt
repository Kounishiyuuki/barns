package com.barns.app.presentation.support

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
 * Support host. Owns minimal local navigation between the support info,
 * phone consultation guidance, and the local consultation draft.
 */
@Composable
fun SupportScreen(
    container: DependencyContainer,
    onBack: () -> Unit,
) {
    var route by remember { mutableStateOf<SupportRoute>(SupportRoute.Main) }

    when (route) {
        SupportRoute.Main -> {
            val viewModel = remember(container) { container.makeSupportViewModel() }
            SupportInfoContent(
                viewModel = viewModel,
                onOpenPhone = { route = SupportRoute.Phone },
                onOpenDraft = { route = SupportRoute.Draft },
                onBack = onBack,
            )
        }
        SupportRoute.Phone -> {
            val viewModel = remember(container) { container.makePhoneInquiryViewModel() }
            PhoneInquiryScreen(viewModel = viewModel, onBack = { route = SupportRoute.Main })
        }
        SupportRoute.Draft -> {
            val viewModel = remember(container) { container.makeConsultationDraftViewModel() }
            ConsultationDraftScreen(viewModel = viewModel, onBack = { route = SupportRoute.Main })
        }
    }
}

@Composable
private fun SupportInfoContent(
    viewModel: SupportViewModel,
    onOpenPhone: () -> Unit,
    onOpenDraft: () -> Unit,
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
            text = "Support",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        when (val current = state) {
            SupportViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SupportViewModel.State.Failed -> {
                Text(text = current.message, style = MaterialTheme.typography.bodyMedium)
            }
            is SupportViewModel.State.Loaded -> {
                val info = current.info
                Text(text = info.displayName, style = MaterialTheme.typography.titleMedium)
                Text(text = info.description, style = MaterialTheme.typography.bodyMedium)
                info.businessHoursNote?.let { hours ->
                    Text(text = hours, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = info.inquiryPolicy,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Button(onClick = onOpenPhone, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Phone consultation")
                }
                Button(onClick = onOpenDraft, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Consultation Draft")
                }
                Text(
                    text = "Consultation drafts are local preparation. Not submitted.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

private sealed interface SupportRoute {
    data object Main : SupportRoute
    data object Phone : SupportRoute
    data object Draft : SupportRoute
}
