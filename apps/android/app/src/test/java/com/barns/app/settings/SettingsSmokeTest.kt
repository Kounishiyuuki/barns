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
        assertEquals(
            listOf("App Status", "About", "Data & Privacy", "Support", "Release Readiness", "Legal"),
            titles,
        )

        viewModel.sections.forEach { section ->
            assertTrue(section.items.isNotEmpty())
            section.items.forEach { item ->
                assertTrue(item.title.isNotBlank())
                assertTrue(item.detail.isNotBlank())
            }
        }
    }

    @Test
    fun appStatusSectionReportsMvpStatus() {
        val viewModel = SettingsViewModel()

        val appItems = viewModel.sections.first { it.title == "App Status" }.items
        assertEquals("Barns MVP · Local-first / mock-first", appItems.first { it.title == "Build" }.detail)
        assertEquals("No account, and no cloud sync.", appItems.first { it.title == "Account" }.detail)
    }

    @Test
    fun dataAndPrivacySectionStatesNoTrackingOrUploads() {
        val viewModel = SettingsViewModel()

        val privacyItems = viewModel.sections.first { it.title == "Data & Privacy" }.items
        assertTrue(privacyItems.any { it.title == "No tracking" })
        assertTrue(privacyItems.any { it.title == "No uploads" })
    }

    @Test
    fun releaseReadinessDoesNotClaimStoreReadiness() {
        val viewModel = SettingsViewModel()

        val readiness = viewModel.sections.first { it.title == "Release Readiness" }.items
        val beforeRelease = readiness.first { it.title == "Before release" }.detail
        assertTrue(beforeRelease.contains("manual QA"))
        assertTrue(beforeRelease.lowercase().contains("release"))
    }
}
