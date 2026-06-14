/// Static, non-sensitive home summary for the MVP source skeleton.
/// Counts and labels are placeholders; no customer-side private data is used.
struct MockHomeRepository: HomeRepository {
    func homeSummary() async throws -> HomeSummary {
        HomeSummary(
            welcomeMessage: "Your after-sales care starts here.",
            registeredItemCount: 0,
            nextCareLabel: "No upcoming care yet",
            supportGuidance: "Need help? Phone consultation guidance will appear here.",
            patternsEntryLabel: "Browse wall greenery patterns"
        )
    }
}
