import XCTest
@testable import Barns

/// Validates that the app ships a minimal, accurate privacy manifest that
/// matches the current local-first / mock-first MVP: no tracking, no tracking
/// domains, no collected data types, and no required-reason API declarations.
///
/// The `BarnsTests` target is hosted by the app, so `Bundle.main` resolves to
/// the app bundle that contains `PrivacyInfo.xcprivacy`.
final class PrivacyManifestSmokeTests: XCTestCase {
    func testPrivacyManifestExistsAndReflectsLocalOnlyMvp() throws {
        let url = try XCTUnwrap(
            Bundle.main.url(forResource: "PrivacyInfo", withExtension: "xcprivacy"),
            "PrivacyInfo.xcprivacy should be bundled with the app target"
        )
        let data = try Data(contentsOf: url)
        let plist = try XCTUnwrap(
            PropertyListSerialization.propertyList(from: data, format: nil) as? [String: Any],
            "PrivacyInfo.xcprivacy should be a valid plist dictionary"
        )

        // No tracking, and no tracking domains.
        XCTAssertEqual(plist["NSPrivacyTracking"] as? Bool, false)
        XCTAssertEqual((plist["NSPrivacyTrackingDomains"] as? [Any])?.isEmpty, true)

        // Nothing is collected/transmitted off device in the current MVP.
        XCTAssertEqual((plist["NSPrivacyCollectedDataTypes"] as? [Any])?.isEmpty, true)

        // No required-reason API usage was found in the audit (in-memory mocks,
        // no UserDefaults / file / networking), so this stays empty.
        XCTAssertEqual((plist["NSPrivacyAccessedAPITypes"] as? [Any])?.isEmpty, true)
    }
}
