# How to deploy LA Metro App

Developer's reference...

1. Commit. Merge to master. Push.
2. Increment version number & code in Android Studio
3. Mount keystore (obviously only I can do that...)
    1. Key alias: metrokey
4. Build->Generate Signed APK
5. Open keyfile. Create release folder.
    1. Copy app-release.apk
    2. Copy build/outputs/mapping/release/mapping.txt