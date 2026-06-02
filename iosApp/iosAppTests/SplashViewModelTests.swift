import XCTest
@testable import Moventiq

@MainActor
final class SplashViewModelTests: XCTestCase {
    func test_initialState_isVisibleAndNotComplete() {
        let viewModel = SplashViewModel(enterWindow: 60, exitDuration: 60)

        XCTAssertEqual(viewModel.state, SplashUiState())
    }

    func test_enterWindow_elapsed_transitionsToExitingThenComplete() async throws {
        let viewModel = SplashViewModel(enterWindow: 0.1, exitDuration: 0.2)

        XCTAssertEqual(viewModel.state, SplashUiState())

        try await waitUntil(timeout: 0.5) {
            viewModel.state.phase == .exiting
        }
        XCTAssertFalse(viewModel.state.isComplete)

        try await waitUntil(timeout: 0.5) {
            viewModel.state.isComplete
        }
        XCTAssertEqual(viewModel.state.phase, .exiting)
    }

    private func waitUntil(
        timeout: TimeInterval,
        condition: @escaping @MainActor () -> Bool,
    ) async throws {
        let deadline = Date().addingTimeInterval(timeout)
        while Date() < deadline {
            if condition() { return }
            try await Task.sleep(nanoseconds: 10_000_000)
        }
        XCTFail("Condition not met within \(timeout)s")
    }

    func test_zeroDelay_completesImmediately() async throws {
        let viewModel = SplashViewModel(enterWindow: 0, exitDuration: 0)

        try await waitUntil(timeout: 0.5) {
            viewModel.state.isComplete
        }
        XCTAssertEqual(viewModel.state.phase, .exiting)
        XCTAssertTrue(viewModel.state.isComplete)
    }

    func test_onEvent_contentDrawn_doesNotChangeState() {
        let viewModel = SplashViewModel(enterWindow: 60, exitDuration: 60)

        viewModel.handle(.contentDrawn)

        XCTAssertEqual(viewModel.state, SplashUiState())
    }
}
