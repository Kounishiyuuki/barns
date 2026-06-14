protocol AuthRepository {
    func currentUser() async throws -> User?
    func signInWithMockAccount() async throws -> User
    func signOut() async throws
}
