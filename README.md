# ADB Toggle Widget

A 1x1 home screen widget that switches USB debugging on and off.

## Setup

1. Build and install (with USB debugging on and the phone connected):

   ```
   ./gradlew installDebug
   ```

2. Grant the secure-settings permission once (it persists across reboots, only lost on uninstall):

   ```
   adb shell pm grant dev.xavier.adbtoggle android.permission.WRITE_SECURE_SETTINGS
   ```

3. Long-press the home screen → Widgets → **ADB Toggle**.

## Notes

- If the setting is changed elsewhere (e.g. in Settings), the widget refreshes on its next periodic update (~30 min) or on the next tap.
- Xiaomi/MIUI also requires "USB debugging (Security settings)" to be enabled before `pm grant` works.
- Turning ADB off from the widget immediately drops any active adb connection.
