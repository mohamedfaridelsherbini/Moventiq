import XCTest
@testable import Moventiq

final class OnboardingUiStateTests: XCTestCase {
    func test_shouldShowOnboarding_whenNotCompletedAndNotFinished() {
        let state = OnboardingUiState(hasCompletedOnboarding: false, isFinished: false)

        XCTAssertTrue(state.shouldShowOnboarding)
    }

    func test_shouldShowOnboarding_isFalse_whenAlreadyCompleted() {
        let state = OnboardingUiState(hasCompletedOnboarding: true, isFinished: false)

        XCTAssertFalse(state.shouldShowOnboarding)
    }

    func test_shouldShowOnboarding_isFalse_whenFinished() {
        let state = OnboardingUiState(hasCompletedOnboarding: false, isFinished: true)

        XCTAssertFalse(state.shouldShowOnboarding)
    }

    func test_currentPageData_clampsOutOfRangeIndex() {
        let state = OnboardingUiState(currentPage: 99)

        XCTAssertEqual(state.currentPageData, .autoSurface)
    }

    func test_preview_defaultsToFirstPageAndIncomplete() {
        let state = OnboardingUiState.preview()

        XCTAssertEqual(state.currentPage, 0)
        XCTAssertTrue(state.shouldShowOnboarding)
    }
}
