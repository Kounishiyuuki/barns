import SwiftUI

/// UI-only guidance screen. No real phone number; does not place a call.
struct PhoneInquiryView: View {
    @StateObject private var viewModel: PhoneInquiryViewModel

    init(viewModel: PhoneInquiryViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        content
            .navigationTitle("Phone consultation")
            .task { await viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ProgressView()
        case .failed(let message):
            Text(message)
                .foregroundStyle(.secondary)
        case .loaded(let info):
            List {
                Section {
                    Text(info.inquiryPolicy)
                }
                Section("How to reach us") {
                    LabeledContent(info.phoneLabel, value: info.phoneNumber ?? "To be announced")
                    if let hours = info.businessHoursNote {
                        Text(hours)
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
                    Text("Calling from the app is not enabled yet. This is guidance for contacting support yourself — no request is submitted from the app, and no customer data is sent automatically.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        PhoneInquiryView(viewModel: DependencyContainer().makePhoneInquiryViewModel())
    }
}
