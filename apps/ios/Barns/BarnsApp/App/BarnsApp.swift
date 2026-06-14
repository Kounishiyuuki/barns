import SwiftUI

@main
struct BarnsApp: App {
    private let environment = AppEnvironment.makeDefault()

    var body: some Scene {
        WindowGroup {
            HomeView(viewModel: environment.dependencyContainer.makeHomeViewModel())
        }
    }
}
