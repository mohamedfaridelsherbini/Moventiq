import XCTest
@testable import Moventiq

@MainActor
final class OnboardingViewModelTests: XCTestCase {
    func test_continue_advancesPages_thenFinishesOnLastPage() {
        let store = FakeOnboardingStatusStore()
        let viewModel = OnboardingViewModel(statusStore: store)

        XCTAssertEqual(viewModel.state.currentPage, 0)

        viewModel.handle(.continue)
        XCTAssertEqual(viewModel.state.currentPage, 1)

        viewModel.handle(.continue)
        XCTAssertEqual(viewModel.state.currentPage, 2)

        viewModel.handle(.continue)
        XCTAssertTrue(viewModel.state.isFinished)
        XCTAssertTrue(store.completed)
    }

    func test_skip_marksOnboardingComplete() {
        let store = FakeOnboardingStatusStore()
        let viewModel = OnboardingViewModel(statusStore: store)

        viewModel.handle(.skip)

        XCTAssertTrue(viewModel.state.isFinished)
        XCTAssertTrue(store.completed)
    }

    func test_loadsCompletedFlagFromPreferences() {
        let store = FakeOnboardingStatusStore(initialCompleted: true)
        let viewModel = OnboardingViewModel(statusStore: store)

        XCTAssertFalse(viewModel.state.shouldShowOnboarding)
    }

    func test_pageChanged_updatesCurrentPage() {
        let viewModel = OnboardingViewModel(statusStore: FakeOnboardingStatusStore())

        viewModel.handle(.pageChanged(2))

        XCTAssertEqual(viewModel.state.currentPage, 2)
    }

    func test_pageChanged_ignoresOutOfRangeIndex() {
        let viewModel = OnboardingViewModel(statusStore: FakeOnboardingStatusStore())

        viewModel.handle(.pageChanged(5))

        XCTAssertEqual(viewModel.state.currentPage, 0)
    }
}

private final class FakeOnboardingStatusStore: OnboardingStatusStore {
    private(set) var completed: Bool

    init(initialCompleted: Bool = false) {
        completed = initialCompleted
    }

    func hasCompletedOnboarding() -> Bool {
        completed
    }

    func setOnboardingCompleted() {
        completed = true
    }
}
