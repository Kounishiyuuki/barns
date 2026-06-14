/// In-memory mock auth. No persistence, no network, no real credentials.
actor MockAuthRepository: AuthRepository {
    private var signedInUser: User?
    private let mockUser = User(id: "mock-user-001", displayName: "Guest")

    func currentUser() async throws -> User? {
        signedInUser
    }

    func signInWithMockAccount() async throws -> User {
        signedInUser = mockUser
        return mockUser
    }

    func signOut() async throws {
        signedInUser = nil
    }
}
