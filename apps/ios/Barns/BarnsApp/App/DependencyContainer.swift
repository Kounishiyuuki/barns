struct DependencyContainer {
    @MainActor
    func makeHomeViewModel() -> HomeViewModel {
        HomeViewModel()
    }
}
