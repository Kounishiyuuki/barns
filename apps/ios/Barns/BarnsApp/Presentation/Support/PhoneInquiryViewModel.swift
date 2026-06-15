import Combine

/// UI-only phone consultation guidance. No real phone number, no tel:// call.
@MainActor
final class PhoneInquiryViewModel: ObservableObject {
    enum State: Equatable {
        case loading
        case loaded(CompanyInfo)
        case failed(String)
    }

    @Published private(set) var state: State = .loading

    private let getSupportInfoUseCase: GetSupportInfoUseCase

    init(getSupportInfoUseCase: GetSupportInfoUseCase) {
        self.getSupportInfoUseCase = getSupportInfoUseCase
    }

    func load() async {
        state = .loading
        do {
            let info = try await getSupportInfoUseCase.execute()
            state = .loaded(info)
        } catch {
            state = .failed("Unable to load guidance. Please try again.")
        }
    }
}
