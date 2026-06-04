# Permission flow — test plan

Status: **tests-green**  
Design: [uml.md](./uml.md)

## Case coverage

| Case # | Description | Unit | UI Android | UI iOS |
|---|---|:---:|:---:|:---:|
| 1 | Fresh → Location | ✅ | ✅ | ✅ |
| 2 | Maybe later → Notification | ✅ | ✅ | ✅ |
| 3 | Not now → Main | ✅ | ✅ | ✅ |
| 4 | Allow → deny → Denied | ✅ | ✅ | ✅ |
| 5 | Grant + refresh → Notification | ✅ | ⏭️ | ⏭️ |
| 6 | Limited features → Main | ✅ | ✅ | ✅ |
| 7 | Granted + Not now → foreground → Notification | ✅ | ⏭️ | ⏭️ |
| 8 | Maybe later + Not now → foreground → Location | ✅ | ⏭️ | ⏭️ |

⏭️ = unit only (lifecycle / foreground simulation)

## Unit tests — Android

| Test name | Class | Case |
|---|---|---|
| `refreshFlow_startsAtLocation_whenFreshStoreAndNoAccess` | PermissionFlowViewModelTest | 1 |
| `locationLater_movesToNotification` | PermissionFlowViewModelTest | 2 |
| `notificationSkip_completesFlow` | PermissionFlowViewModelTest | 3 |
| `locationResults_denied_showsDeniedScreen` | PermissionFlowViewModelTest | 4 |
| `locationResults_granted_thenRefresh_movesToNotification` | PermissionFlowViewModelTest | 5 |
| `deniedLimitedFeatures_completesFlow` | PermissionFlowViewModelTest | 6 |
| `locationGranted_thenNotificationSkip_thenAppForeground_showsNotificationAgain` | PermissionFlowViewModelTest | 7 |
| `appForeground_afterMaybeLaterAndNotNow_reShowsLocation` | PermissionFlowViewModelTest | 8 |
| `resolve_*` | PermissionFlowStepResolverTest | all |

## Unit tests — iOS

Mirror Android cases in `PermissionFlowViewModelTests` and `PermissionFlowStepResolverTests`.

## UI tests — Android

| Test name | Class | Case |
|---|---|---|
| `app_showsLocationPermission_afterOnboardingSkipped` | PermissionFlowTest | 1 |
| `app_showsNotification_afterLocationLater` | PermissionFlowTest | 2 |
| `app_reachesHome_afterPermissionSkips` | PermissionFlowTest | 3 |
| `app_reachesHome_afterDeniedLimitedFeatures` | PermissionFlowTest | 4, 6 |
| `locationPermission_showsHeadline` | PermissionContentTest | smoke |
| `notificationPermission_showsHeadline` | PermissionContentTest | smoke |
| `permissionDenied_showsHeadline` | PermissionContentTest | smoke |

## UI tests — iOS

| Test name | Class | Case |
|---|---|---|
| `test_showsLocationPermission_afterOnboardingSkip` | PermissionFlowUITests | 1 |
| `test_showsNotification_afterLocationLater` | PermissionFlowUITests | 2 |
| `test_reachesHome_afterPermissionSkips` | PermissionFlowUITests | 3 |
| `test_reachesHome_afterDeniedLimitedFeatures` | PermissionFlowUITests | 4, 6 |
