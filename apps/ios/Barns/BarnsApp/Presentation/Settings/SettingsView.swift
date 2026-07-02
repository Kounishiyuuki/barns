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
                        // Stacked title + full-width detail. Long details (e.g.
                        // the About / Privacy notes) read far better wrapping
                        // across the row than squeezed into a trailing value,
                        // and this mirrors the Android headline / supporting
                        // layout for cross-platform parity.
                        VStack(alignment: .leading, spacing: 4) {
                            Text(item.title)
                            Text(item.detail)
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }
                        .padding(.vertical, 2)
                        .accessibilityElement(children: .combine)
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
