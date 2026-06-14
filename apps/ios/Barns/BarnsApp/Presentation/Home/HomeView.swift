import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel

    init(viewModel: HomeViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        NavigationStack {
            List {
                Section("Today") {
                    Text(viewModel.state.title)
                        .font(.headline)
                    Text(viewModel.state.message)
                        .foregroundStyle(.secondary)
                }
            }
            .navigationTitle("barns")
        }
    }
}

#Preview {
    HomeView(viewModel: HomeViewModel())
}
