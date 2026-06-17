import XCTest
@testable import Barns

/// Smoke tests for the read-only Patterns data and Support info (local-only).
final class PatternsSupportSmokeTests: XCTestCase {
    func testPatternsLoad() async throws {
        let repository = MockPatternRepository()
        let getPatterns = GetPatternsUseCase(repository: repository)

        let patterns = try await getPatterns.execute()
        XCTAssertFalse(patterns.isEmpty)

        let first = try XCTUnwrap(patterns.first)
        XCTAssertFalse(first.name.isEmpty)
    }

    func testSupportInfoLoadsLocalFirst() async throws {
        let repository = MockSupportRepository()
        let getSupportInfo = GetSupportInfoUseCase(repository: repository)

        let info = try await getSupportInfo.execute()
        XCTAssertFalse(info.displayName.isEmpty)
        // No real contact data is committed; phone/address stay null.
        XCTAssertNil(info.phoneNumber)
        XCTAssertNil(info.address)
    }
}
