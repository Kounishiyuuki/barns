package com.barns.app.settings

import com.barns.app.presentation.settings.SettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke test for the static, local-only Settings screen. No network, no
 * repository, no persistence: just the fixed presentation sections.
 */
class SettingsSmokeTest {

    @Test
    fun exposesExpectedStaticSections() {
        val viewModel = SettingsViewModel()

        val titles = viewModel.sections.map { it.title }
        assertEquals(listOf("App", "Support", "Privacy", "Development"), titles)

        viewModel.sections.forEach { section ->
            assertTrue(section.items.isNotEmpty())
            section.items.forEach { item ->
                assertTrue(item.title.isNotBlank())
                assertTrue(item.detail.isNotBlank())
            }
        }
    }

    @Test
    fun appSectionReportsMvpStatus() {
        val viewModel = SettingsViewModel()

        val appItems = viewModel.sections.first { it.title == "App" }.items
        assertEquals("Barns MVP", appItems.first { it.title == "Name" }.detail)
        assertEquals("Local-first / mock-first", appItems.first { it.title == "Mode" }.detail)
    }
}
