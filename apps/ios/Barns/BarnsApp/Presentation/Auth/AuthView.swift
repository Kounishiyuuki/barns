import SwiftUI

struct AuthView: View {
    @StateObject private var viewModel: AuthViewModel

    init(viewModel: AuthViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        VStack(spacing: 24) {
            Spacer()
            Text("barns")
                .font(.largeTitle.bold())
            Text("After-sales care for your greenery")
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)

            if case .failed(let message) = viewModel.state {
                Text(message)
                    .font(.footnote)
                    .foregroundStyle(.red)
            }

            Spacer()

            Button {
                Task { await viewModel.loginAsGuest() }
            } label: {
                if viewModel.state == .loading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                } else {
                    Text("Continue as guest")
                        .frame(maxWidth: .infinity)
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(viewModel.state == .loading)
        }
        .padding()
    }
}

#Preview {
    AuthView(viewModel: DependencyContainer().makeAuthViewModel())
}
