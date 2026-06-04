import XCTest

private enum PermissionAccessibilityId {
    static let locationScreen = "permission_location_screen"
    static let locationLater = "permission_location_later"
    static let notificationScreen = "permission_notification_screen"
    static let notificationSkip = "permission_notification_skip"
    static let deniedScreen = "permission_denied_screen"
    static let deniedLimited = "permission_denied_limited"
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

    func test_showsNotification_afterLocationLater() {
        let skip = app.descendants(matching: .any)[OnboardingAccessibilityId.skip]
        XCTAssertTrue(skip.waitForExistence(timeout: 5))
        skip.tap()

        let locationLater = app.descendants(matching: .any)[PermissionAccessibilityId.locationLater]
        XCTAssertTrue(locationLater.waitForExistence(timeout: 5))
        locationLater.tap()

        let notificationScreen = app.descendants(matching: .any)[PermissionAccessibilityId.notificationScreen]
        XCTAssertTrue(notificationScreen.waitForExistence(timeout: 5))
    }

    func test_reachesHome_afterPermissionSkips() {
        let skip = app.descendants(matching: .any)[OnboardingAccessibilityId.skip]
        XCTAssertTrue(skip.waitForExistence(timeout: 5))
        skip.tap()

        let locationLater = app.descendants(matching: .any)[PermissionAccessibilityId.locationLater]
        XCTAssertTrue(locationLater.waitForExistence(timeout: 5))
        locationLater.tap()

        let notificationSkip = app.descendants(matching: .any)[PermissionAccessibilityId.notificationSkip]
        XCTAssertTrue(notificationSkip.waitForExistence(timeout: 5))
        notificationSkip.tap()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }

    func test_reachesHome_afterDeniedLimitedFeatures() {
        app.terminate()
        app.launchArguments += ["-UITestPermissionDenied"]
        app.launch()

        app.descendants(matching: .any)[OnboardingAccessibilityId.skip].tap()

        let deniedScreen = app.descendants(matching: .any)[PermissionAccessibilityId.deniedScreen]
        XCTAssertTrue(deniedScreen.waitForExistence(timeout: 5))
        app.descendants(matching: .any)[PermissionAccessibilityId.deniedLimited].tap()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }
}
