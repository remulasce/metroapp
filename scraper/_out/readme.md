Outputt'ed .db files go here.

They will need to be moved to lametroapp/app/src/main/assets/databases to get them into the Android app.
Don't forget to increment the sqlite DB version in the app, 2 locations:
- app\src\main\java\com\remulasce\lametroapp\static_data\SQLPreloadedStopsReader.java
- app\src\main\java\com\remulasce\lametroapp\static_data\InstalledAgencyLoader.java