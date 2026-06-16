package com.barns.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import com.barns.app.app.BarnsApp
import com.barns.app.presentation.root.RootScreen

/**
 * Compose host. Obtains the app environment / DI container from the
 * Application and renders the existing RootScreen. No business logic here.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as BarnsApp).environment.dependencyContainer
        setContent {
            MaterialTheme {
                val authViewModel = remember { container.makeAuthViewModel() }
                RootScreen(authViewModel = authViewModel, container = container)
            }
        }
    }
}
