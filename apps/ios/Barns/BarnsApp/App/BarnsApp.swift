import SwiftUI

@main
struct BarnsApp: App {
    private let environment = AppEnvironment.makeDefault()

    var body: some Scene {
        WindowGroup {
            RootView(
                authViewModel: environment.dependencyContainer.makeAuthViewModel(),
                container: environment.dependencyContainer
            )
        }
    }
}
