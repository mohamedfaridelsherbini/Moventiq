import XCTest
@testable import Moventiq

@MainActor
final class PermissionFlowViewModelTests: XCTestCase {
    override func setUp() {
        super.setUp()
        PermissionFlowViewModel.resetSessionForTests()
    }

    func test_locationResults_denied_showsDeniedScreen() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationResults(fineGranted: false, backgroundGranted: false))

        XCTAssertEqual(viewModel.state.step, .denied)
    }

    func test_locationResults_whenInUseOnly_showsDeniedScreen() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationResults(fineGranted: true, backgroundGranted: false))

        XCTAssertEqual(viewModel.state.step, .denied)
    }

    func test_locationResults_granted_thenRefresh_movesToNotification() {
        let checker = FakePermissionStatusChecker()
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: checker,
        )

        viewModel.handle(.locationResults(fineGranted: true, backgroundGranted: true))
        checker.adequateLocation = true
        viewModel.handle(.refresh)

        XCTAssertEqual(viewModel.state.step, .notification)
    }

    func test_notificationResult_granted_completesFlow() {
        let checker = FakePermissionStatusChecker()
        checker.adequateLocation = true
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: checker,
        )

        checker.notificationGranted = true
        viewModel.handle(.notificationResult(granted: true))

        XCTAssertEqual(viewModel.state.step, .none)
    }

    func test_deniedLimitedFeatures_completesFlow() {
        let store = FreshPermissionStatusStore()
        store.setShowLocationDeniedScreen(true)
        let viewModel = PermissionFlowViewModel(
            statusStore: store,
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.deniedLimitedFeatures)

        XCTAssertEqual(viewModel.state.step, .none)
    }

    func test_appForeground_afterLimitedFeatures_staysComplete() {
        let store = FreshPermissionStatusStore()
        store.setLimitedFeaturesAcknowledged()
        let viewModel = PermissionFlowViewModel(
            statusStore: store,
            statusChecker: FakePermissionStatusChecker(),
        )

        XCTAssertEqual(viewModel.state.step, .none)

        viewModel.handle(.appReturnedFromBackground)

        XCTAssertEqual(viewModel.state.step, .none)
    }

    func test_startsAtLocation_whenFreshStoreAndNoAccess() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        XCTAssertEqual(viewModel.state.step, .location)
    }

    func test_completes_whenPromptsAlreadyCompleted() {
        let checker = FakePermissionStatusChecker()
        checker.adequateLocation = true
        checker.notificationGranted = true
        let viewModel = PermissionFlowViewModel(
            statusStore: CompletedPermissionStatusStore(),
            statusChecker: checker,
        )

        XCTAssertTrue(viewModel.state.isFlowComplete)
    }

    func test_locationLater_movesToNotification() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationLater)

        XCTAssertEqual(viewModel.state.step, .notification)
    }

    func test_locationLater_doesNotShowDeniedScreen() {
        let store = FreshPermissionStatusStore()
        let viewModel = PermissionFlowViewModel(
            statusStore: store,
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationLater)

        XCTAssertEqual(viewModel.state.step, .notification)
        XCTAssertFalse(store.shouldShowLocationDeniedScreen())
    }

    func test_locationGranted_thenNotificationSkip_thenAppForeground_showsNotificationAgain() {
        let checker = FakePermissionStatusChecker()
        checker.adequateLocation = true
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: checker,
        )

        viewModel.handle(.notificationSkip)
        XCTAssertEqual(viewModel.state.step, .none)

        viewModel.handle(.appReturnedFromBackground)

        XCTAssertEqual(viewModel.state.step, .notification)
    }

    func test_appForeground_afterMaybeLaterAndNotNow_reShowsLocation() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationLater)
        viewModel.handle(.notificationSkip)
        XCTAssertEqual(viewModel.state.step, .none)

        viewModel.handle(.appReturnedFromBackground)

        XCTAssertEqual(viewModel.state.step, .location)
    }

    func test_refresh_afterMaybeLater_keepsNotificationInSameSession() {
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: FakePermissionStatusChecker(),
        )

        viewModel.handle(.locationLater)
        viewModel.handle(.refresh)

        XCTAssertEqual(viewModel.state.step, .notification)
    }

    func test_coldStart_osDenied_showsDeniedScreen() {
        let checker = FakePermissionStatusChecker()
        checker.locationPermissionDenied = true
        let viewModel = PermissionFlowViewModel(
            statusStore: FreshPermissionStatusStore(),
            statusChecker: checker,
        )

        XCTAssertEqual(viewModel.state.step, .denied)
    }
}
