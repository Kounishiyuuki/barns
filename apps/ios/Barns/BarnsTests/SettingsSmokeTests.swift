import XCTest
@testable import Barns

/// Smoke tests for the static, local-only Settings screen.
@MainActor
final class SettingsSmokeTests: XCTestCase {
    func testExposesExpectedStaticSections() {
        let viewModel = SettingsViewModel()

        let titles = viewModel.sections.map(\.title)
        XCTAssertEqual(titles, ["App", "About", "Privacy", "Support", "Legal", "Development"])

        // Every section has at least one item, and no item is empty.
        for section in viewModel.sections {
            XCTAssertFalse(section.items.isEmpty)
            for item in section.items {
                XCTAssertFalse(item.title.isEmpty)
                XCTAssertFalse(item.detail.isEmpty)
            }
        }
    }

    func testAppSectionReportsMvpStatus() {
        let viewModel = SettingsViewModel()

        let appItems = try? XCTUnwrap(viewModel.sections.first { $0.title == "App" }).items
        XCTAssertEqual(appItems?.first { $0.title == "Name" }?.detail, "Barns MVP")
        XCTAssertEqual(appItems?.first { $0.title == "Mode" }?.detail, "Local-first / mock-first")
    }
}
