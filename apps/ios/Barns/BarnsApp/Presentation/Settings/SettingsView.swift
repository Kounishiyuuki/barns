import SwiftUI

/// Minimal, local-only Settings screen for the MVP. Static informational
/// content only: app status, support guidance, privacy note, and a
/// development note. No external links, no tel:/mailto:, no real contact data.
struct SettingsView: View {
    @StateObject private var viewModel: SettingsViewModel

    init(viewModel: SettingsViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        List {
            ForEach(viewModel.sections) { section in
                Section(section.title) {
                    ForEach(section.items) { item in
                        LabeledContent(item.title) {
                            Text(item.detail)
                                .multilineTextAlignment(.trailing)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
            }
        }
        .navigationTitle("Settings")
    }
}

#Preview {
    NavigationStack {
        SettingsView(viewModel: DependencyContainer().makeSettingsViewModel())
    }
}
