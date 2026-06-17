import XCTest
@testable import Barns

/// Smoke tests for the mock auth flow (local-only, no real auth).
final class AuthSmokeTests: XCTestCase {
    func testCurrentUserIsNilBeforeLogin() async throws {
        let repository = MockAuthRepository()
        let getCurrentUser = GetCurrentUserUseCase(authRepository: repository)

        let user = try await getCurrentUser.execute()

        XCTAssertNil(user)
    }

    func testGuestLoginExposesUser() async throws {
        let repository = MockAuthRepository()
        let loginAsGuest = LoginAsGuestUseCase(authRepository: repository)
        let getCurrentUser = GetCurrentUserUseCase(authRepository: repository)

        let user = try await loginAsGuest.execute()
        XCTAssertFalse(user.id.isEmpty)
        XCTAssertFalse(user.displayName.isEmpty)

        let current = try await getCurrentUser.execute()
        XCTAssertEqual(current, user)
    }
}
