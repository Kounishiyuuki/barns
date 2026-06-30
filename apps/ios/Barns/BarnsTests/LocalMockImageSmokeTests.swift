import XCTest
@testable import Barns

/// Smoke tests for the local mock image reference resolver. These verify only
/// the pure `mock://…` reference parsing — bundled-asset loading is exercised
/// at runtime/QA, not here. Mock/demo, local-only; no network involved.
final class LocalMockImageSmokeTests: XCTestCase {
    func testResolvesAssetNameFromMockReference() {
        let reference = URL(string: "mock://catalog/catalog-office-vertical-green-wall-01")
        XCTAssertEqual(
            LocalMockImage.assetName(for: reference),
            "catalog-office-vertical-green-wall-01"
        )
    }

    func testNilReferenceResolvesToNoAsset() {
        XCTAssertNil(LocalMockImage.assetName(for: nil))
    }

    func testNonMockSchemeIsRejected() {
        // Guardrail: only local `mock://` references are honored; remote URLs
        // must never resolve to a loadable asset (no network image loading).
        XCTAssertNil(LocalMockImage.assetName(for: URL(string: "https://example.com/x.jpg")))
        XCTAssertNil(LocalMockImage.assetName(for: URL(string: "http://example.com/x.jpg")))
    }

    func testSeededProductItemsCarryLocalMockReferences() async throws {
        let items = try await MockProductItemRepository().productItems()
        // Seeds keep their stable count; mapped images use the local scheme.
        XCTAssertEqual(items.count, 2)
        for item in items {
            if let reference = item.imageUrl {
                XCTAssertEqual(reference.scheme, "mock")
                XCTAssertNotNil(LocalMockImage.assetName(for: reference))
            }
        }
    }

    func testSeededCatalogItemsUseLocalMockOrNilReferences() async throws {
        let items = try await MockCatalogRepository().catalogItems()
        XCTAssertFalse(items.isEmpty)
        for item in items {
            // Either no image (safe null fallback) or a local mock reference;
            // never a remote URL.
            if let reference = item.imageUrl {
                XCTAssertEqual(reference.scheme, "mock")
            }
        }
    }
}
