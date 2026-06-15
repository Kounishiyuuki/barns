package com.barns.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "barns", style = MaterialTheme.typography.displaySmall)
        Text(
            text = "After-sales care for your greenery",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        if (state is AuthViewModel.State.Failed) {
            Text(
                text = (state as AuthViewModel.State.Failed).message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is AuthViewModel.State.Loading,
            onClick = viewModel::loginAsGuest,
        ) {
            if (state is AuthViewModel.State.Loading) {
                CircularProgressIndicator()
            } else {
                Text("Continue as guest")
            }
        }
    }
}
