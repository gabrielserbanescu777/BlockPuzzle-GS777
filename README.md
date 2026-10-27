# Block Puzzle — GS777 v0.7 (ethical gameplay)

A free, offline 10×10 puzzle with tap and drag controls, optional daily puzzle, local achievements and optional rewarded continuation. No mandatory daily login, streak loss, time pressure, loot boxes, real-money wagers or push notifications.

## Advertising policy
- Debug builds use Google sample test ad unit IDs.
- App Open ads intentionally **disabled** to avoid interruption on launch.
- Interstitial shown **only after the user requests a new game**, once per five completed games at most, with a 5-minute cooldown; never during active play.
- Rewarded ad is entirely optional and provides one explicitly described continuation per game.
- UMP consent requested before ads initialization.
- Release requires privacy policy, consent verification, device QA, Play Console Data safety declarations and signed AAB.

## Build
Use JDK 17, Android SDK 35, Gradle 8.10.2, run `gradle assembleDebug`. Or push the folder contents to a GitHub repository and run the included GitHub Actions workflow. This environment has not compiled the APK.
