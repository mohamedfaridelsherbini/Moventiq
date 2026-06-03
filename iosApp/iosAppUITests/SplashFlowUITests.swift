import XCTest

final class SplashFlowUITests: XCTestCase {
    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    func test_app_navigatesToHome_afterSplashCompletes() {
        let app = XCUIApplication()
        app.launchArguments.append(contentsOf: ["-UITestInstantSplash", "-UITestSkipOnboarding"])
        app.launch()

        let home = app.descendants(matching: .any)["home_screen"]
        XCTAssertTrue(home.waitForExistence(timeout: 5))
    }

    func test_app_showsSplash_beforeNavigationCompletes() {
        let app = XCUIApplication()
        app.launchArguments.append(contentsOf: ["-UITestLongSplash", "-UITestSkipOnboarding"])
        app.launch()

        let splash = app.descendants(matching: .any)["splash_screen"]
        XCTAssertTrue(splash.waitForExistence(timeout: 5))
    }
}
