# How to deploy LA Metro App

Developer's reference...

1. Commit. Merge to master. Push.
2. Final tests on-device
2. Increment version number & code in Android Studio
3. Mount keystore (obviously only I can do that...)
    1. Key alias: metrokey
4. Build -> Generate Signed APK
5. Open keyfile. Create release folder.
    1. Copy app-release.apk
    2. Copy build/outputs/mapping/release/mapping.txt
6. Release on Play Store
    1. Open play.google.com/apps/publish for dev console
    2. Open Release Management -> App Releases -> Manage _release type_
    3. Click "Create Release" button in top right
    4. Drap and drop .apk
    5. Type up "What's new" from log or records
7. Create release in Github