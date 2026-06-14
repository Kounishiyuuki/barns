struct AppEnvironment {
    let dependencyContainer: DependencyContainer

    static func makeDefault() -> AppEnvironment {
        AppEnvironment(dependencyContainer: DependencyContainer())
    }
}
