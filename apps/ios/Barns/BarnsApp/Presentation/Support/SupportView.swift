import SwiftUI

struct SupportView: View {
    @StateObject private var viewModel: SupportViewModel
    private let container: DependencyContainer

    init(viewModel: SupportViewModel, container: DependencyContainer) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.container = container
    }

    var body: some View {
        content
            .navigationTitle("Support")
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
                Section(info.displayName) {
                    Text(info.description)
                        .foregroundStyle(.secondary)
                    if let hours = info.businessHoursNote {
                        Text(hours)
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
                }
                Section("Consultation") {
                    Text(info.inquiryPolicy)
                        .foregroundStyle(.secondary)
                    NavigationLink("Phone consultation") {
                        PhoneInquiryView(viewModel: container.makePhoneInquiryViewModel())
                    }
                    NavigationLink("Consultation draft") {
                        ConsultationDraftView(viewModel: container.makeConsultationDraftViewModel())
                    }
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        SupportView(
            viewModel: DependencyContainer().makeSupportViewModel(),
            container: DependencyContainer()
        )
    }
}
