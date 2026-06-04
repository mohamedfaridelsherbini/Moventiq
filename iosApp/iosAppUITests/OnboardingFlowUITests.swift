import XCTest

private enum OnboardingAccessibilityId {
    static let screen = "onboarding_screen"
    static let skip = "onboarding_skip"
    static let `continue` = "onboarding_continue"
}

final class OnboardingFlowUITests: XCTestCase {
    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    func test_app_showsOnboarding_afterSplashCompletes() {
        let app = launchApp()

        let onboarding = app.descendants(matching: .any)[OnboardingAccessibilityId.screen]
        XCTAssertTrue(onboarding.waitForExistence(timeout: 5))
    }

    func test_app_navigatesToHome_whenOnboardingSkipped() {
        let app = launchApp(skipPermissions: true)

        let onboarding = app.descendants(matching: .any)[OnboardingAccessibilityId.screen]
        XCTAssertTrue(onboarding.waitForExistence(timeout: 5))

        app.buttons[OnboardingAccessibilityId.skip].tap()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }

    func test_app_advancesOnboarding_whenContinueTapped() {
        let app = launchApp()

        let onboarding = app.descendants(matching: .any)[OnboardingAccessibilityId.screen]
        XCTAssertTrue(onboarding.waitForExistence(timeout: 5))

        XCTAssertTrue(app.staticTexts["Create tasks linked to places"].waitForExistence(timeout: 2))
        app.buttons[OnboardingAccessibilityId.continue].tap()
        XCTAssertTrue(app.staticTexts["Moventiq detects when you arrive"].waitForExistence(timeout: 2))
    }

    func test_app_navigatesToHome_afterOnboardingCompletes() {
        let app = launchApp(skipPermissions: true)

        let onboarding = app.descendants(matching: .any)[OnboardingAccessibilityId.screen]
        XCTAssertTrue(onboarding.waitForExistence(timeout: 5))

        let continueButton = app.buttons[OnboardingAccessibilityId.continue]
        continueButton.tap()
        continueButton.tap()
        continueButton.tap()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }

    private func launchApp(skipPermissions: Bool = false) -> XCUIApplication {
        let app = XCUIApplication()
        app.launchArguments.append(contentsOf: ["-UITestInstantSplash", "-UITestFreshOnboarding"])
        if skipPermissions {
            app.launchArguments.append("-UITestSkipPermissions")
        }
        app.launch()
        return app
    }
}
