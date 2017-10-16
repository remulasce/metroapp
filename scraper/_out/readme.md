Outputt'ed .db files go here.

They will need to be moved to lametroapp/app/src/main/assets/databases to get them into the Android app.
Don't forget to increment the sqlite DB version in the app, 2 locations:
- app\src\main\java\com\remulasce\lametroapp\static_data\SQLPreloadedStopsReader.java
- app\src\main\java\com\remulasce\lametroapp\static_data\InstalledAgencyLoader.

If Android Studio gives trouble about not updating the files themselves, you may need to delete everything in the assets/databases folder and then put it back in again, because awful file watching, apparently.