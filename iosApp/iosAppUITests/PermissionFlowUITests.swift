import XCTest

private enum PermissionAccessibilityId {
    static let locationScreen = "permission_location_screen"
    static let locationLater = "permission_location_later"
    static let notificationSkip = "permission_notification_skip"
}

private enum OnboardingAccessibilityId {
    static let skip = "onboarding_skip"
}

final class PermissionFlowUITests: XCTestCase {
    private var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments += ["-UITestInstantSplash", "-UITestFreshOnboarding"]
        app.launch()
    }

    func test_showsLocationPermission_afterOnboardingSkip() {
        let skip = app.descendants(matching: .any)[OnboardingAccessibilityId.skip]
        XCTAssertTrue(skip.waitForExistence(timeout: 5))
        skip.tap()

        let locationScreen = app.descendants(matching: .any)[PermissionAccessibilityId.locationScreen]
        XCTAssertTrue(locationScreen.waitForExistence(timeout: 5))
    }

    func test_reachesHome_afterPermissionSkips() {
        app.descendants(matching: .any)[OnboardingAccessibilityId.skip].tap()
        app.descendants(matching: .any)[PermissionAccessibilityId.locationLater].tap()
        app.descendants(matching: .any)[PermissionAccessibilityId.notificationSkip].tap()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }
}
