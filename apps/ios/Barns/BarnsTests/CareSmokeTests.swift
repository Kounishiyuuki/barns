import XCTest
@testable import Barns

/// Smoke tests for the local-only Care flow.
final class CareSmokeTests: XCTestCase {
    func testCareTasksLoadAndDetailMatches() async throws {
        let repository = MockCareRepository()
        let getTasks = GetCareTasksUseCase(repository: repository)
        let getDetail = GetCareTaskDetailUseCase(repository: repository)

        let tasks = try await getTasks.execute()
        XCTAssertFalse(tasks.isEmpty)

        let first = try XCTUnwrap(tasks.first)
        let detail = try await getDetail.execute(id: first.id)
        XCTAssertEqual(detail, first)
    }

    func testCompleteCareTaskUpdatesStatus() async throws {
        let repository = MockCareRepository()
        let getTasks = GetCareTasksUseCase(repository: repository)
        let completeTask = CompleteCareTaskUseCase(repository: repository)
        let getDetail = GetCareTaskDetailUseCase(repository: repository)

        let tasks = try await getTasks.execute()
        let first = try XCTUnwrap(tasks.first)
        XCTAssertNotEqual(first.status, .completed)

        try await completeTask.execute(id: first.id)

        let updated = try await getDetail.execute(id: first.id)
        XCTAssertEqual(updated?.status, .completed)
    }
}
