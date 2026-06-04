import XCTest
@testable import Moventiq

final class PermissionFlowStepResolverTests: XCTestCase {
    func test_hidesAll_whenLimitedFeaturesAcknowledged() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(limitedFeaturesAcknowledged: true),
        )
        XCTAssertEqual(step, .none)
    }

    func test_completes_whenNotificationGranted() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess: true,
                notificationGranted: true,
            ),
        )
        XCTAssertEqual(step, .none)
    }

    func test_showsLocation_whenNoAccessAndNotSkipped() {
        let step = PermissionFlowStepResolver.resolve(defaultInput())
        XCTAssertEqual(step, .location)
    }

    func test_showsNotification_whenLocationSkippedAndNoAccess() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(locationSkippedThisSession: true),
        )
        XCTAssertEqual(step, .notification)
    }

    func test_showsNotification_whenLocationGrantedAndNotificationNotSkipped() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(hasAdequateLocationAccess: true),
        )
        XCTAssertEqual(step, .notification)
    }

    func test_hidesNotification_whenLocationGrantedAndNotificationSkipped() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess: true,
                notificationSkippedThisSession: true,
            ),
        )
        XCTAssertEqual(step, .none)
    }

    func test_showsDenied_whenRecoveryRequired() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(showLocationDeniedRecovery: true),
        )
        XCTAssertEqual(step, .denied)
    }

    func test_showsNotificationAfterLocationGrant_evenWhenLocationWasSkippedEarlier() {
        let step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess: true,
                locationSkippedThisSession: true,
            ),
        )
        XCTAssertEqual(step, .notification)
    }

    private func defaultInput(
        limitedFeaturesAcknowledged: Bool = false,
        hasAdequateLocationAccess: Bool = false,
        showLocationDeniedRecovery: Bool = false,
        locationSkippedThisSession: Bool = false,
        notificationSkippedThisSession: Bool = false,
        notificationPromptRequired: Bool = true,
        notificationGranted: Bool = false,
    ) -> PermissionFlowInput {
        PermissionFlowInput(
            limitedFeaturesAcknowledged: limitedFeaturesAcknowledged,
            hasAdequateLocationAccess: hasAdequateLocationAccess,
            showLocationDeniedRecovery: showLocationDeniedRecovery,
            locationSkippedThisSession: locationSkippedThisSession,
            notificationSkippedThisSession: notificationSkippedThisSession,
            notificationPromptRequired: notificationPromptRequired,
            notificationGranted: notificationGranted,
        )
    }
}
