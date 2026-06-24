import SwiftUI

/// Shared summary block for a registered greenery, used by the active
/// My Greenery list and the Archived Greenery list so both present the same
/// name / ownership / category / location rhythm. Presentation-only: it reads
/// a `ProductItemPresentation` and never touches data sources.
struct GreenerySummaryView: View {
    let display: ProductItemPresentation

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(display.name)
                .font(.headline)
            Text(display.ownershipSummary)
                .font(.subheadline)
                .foregroundStyle(.secondary)
            HStack(spacing: 6) {
                Label(display.categoryLabel, systemImage: "leaf")
                Text("·")
                Label(display.locationLabel, systemImage: "mappin.and.ellipse")
            }
            .font(.caption)
            .foregroundStyle(.secondary)
        }
    }
}
