import XCTest
@testable import Barns

/// Smoke tests for the static, local-only Settings screen.
@MainActor
final class SettingsSmokeTests: XCTestCase {
    func testExposesExpectedStaticSections() {
        let viewModel = SettingsViewModel()

        let titles = viewModel.sections.map(\.title)
        XCTAssertEqual(
            titles,
            ["App Status", "About", "Data & Privacy", "Support", "Release Readiness", "Legal"]
        )

        // Every section has at least one item, and no item is empty.
        for section in viewModel.sections {
            XCTAssertFalse(section.items.isEmpty)
            for item in section.items {
                XCTAssertFalse(item.title.isEmpty)
                XCTAssertFalse(item.detail.isEmpty)
            }
        }
    }

    func testAppStatusSectionReportsMvpStatus() {
        let viewModel = SettingsViewModel()

        let appItems = try? XCTUnwrap(viewModel.sections.first { $0.title == "App Status" }).items
        XCTAssertEqual(appItems?.first { $0.title == "Build" }?.detail, "Barns MVP · Local-first / mock-first")
        XCTAssertEqual(appItems?.first { $0.title == "Account" }?.detail, "No account, and no cloud sync.")
    }

    func testDataAndPrivacySectionStatesNoTrackingOrUploads() {
        let viewModel = SettingsViewModel()

        let privacyItems = try? XCTUnwrap(viewModel.sections.first { $0.title == "Data & Privacy" }).items
        // The MVP explicitly communicates no analytics/tracking and no uploads.
        XCTAssertTrue(privacyItems?.contains { $0.title == "No tracking" } ?? false)
        XCTAssertTrue(privacyItems?.contains { $0.title == "No uploads" } ?? false)
    }

    func testReleaseReadinessDoesNotClaimStoreReadiness() {
        let viewModel = SettingsViewModel()

        let readiness = try? XCTUnwrap(viewModel.sections.first { $0.title == "Release Readiness" }).items
        // Internal demo-ready only; store release still needs manual work.
        let beforeRelease = readiness?.first { $0.title == "Before release" }?.detail ?? ""
        XCTAssertTrue(beforeRelease.contains("manual QA"))
        XCTAssertTrue(beforeRelease.lowercased().contains("release"))
    }
}
