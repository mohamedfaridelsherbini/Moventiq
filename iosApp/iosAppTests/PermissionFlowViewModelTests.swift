import XCTest
@testable import Moventiq

@MainActor
final class PermissionFlowViewModelTests: XCTestCase {
    func test_startsAtLocation_whenFreshStoreAndNoAccess() async {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: IOSPermissionStatusChecker(),
        )
        await viewModel.refreshFlow()

        XCTAssertEqual(viewModel.state.step, .location)
    }

    func test_completes_whenPromptsAlreadyCompleted() async {
        let viewModel = PermissionFlowViewModel(
            statusStore: CompletedPermissionStatusStore(),
            statusChecker: IOSPermissionStatusChecker(),
        )
        await viewModel.refreshFlow()

        XCTAssertTrue(viewModel.state.isFlowComplete)
    }

    func test_locationLater_movesToNotification() async {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: IOSPermissionStatusChecker(),
        )
        await viewModel.refreshFlow()
        viewModel.handle(.locationLater)
        await viewModel.refreshFlow()

        XCTAssertEqual(viewModel.state.step, .notification)
    }
}
