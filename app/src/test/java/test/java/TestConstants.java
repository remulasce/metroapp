package test.java;

/**
 * Created by Remulasce on 3/17/2015.
 *
 * Just a place to put some strings.
 */
public class TestConstants {


    public static final String PREDICTIONS_NO_SUCH_STOPID_ERROR =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n" +
            "<body copyright=\"All data copyright Los Angeles Rail 2015.\">\n" +
            "<Error shouldRetry=\"false\">\n" +
            "  stopId=803011 is not valid for agency=lametro-rail\n" +
            "</Error>\n" +
            "</body>\n";

    public static final String ROUTECONFIG_NO_SUCH_ROUTE_ERROR = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n" +
            "<body copyright=\"All data copyright Los Angeles Rail 2015.\">\n" +
            "<Error shouldRetry=\"false\">\n" +
            "  Could not get route \"8031\" for agency tag \"lametro-rail\". \n" +
            "One of the tags could be bad.\n" +
            "</Error>\n" +
            "</body>";

    public static final String GREEN_REDONDO_BEACH_STOPID = "80301";
    public static final String GREEN_REDONDO_BEACH_REQUEST = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=lametro-rail&stopId=80301";
    // 2 arrivals to Norwalk, 1 arrival to Redondo Beach
    public static final String GREEN_REDONDO_BEACH_RESPONSE_0 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n" +
            "<body copyright=\"All data copyright Los Angeles Rail 2015.\">\n" +
            "<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Green Line\" routeTag=\"803\" stopTitle=\"Redondo Beach Station\" stopTag=\"80301_0\">\n" +
            "  <direction title=\"803 Norwalk Station\">\n" +
            "  <prediction epochTime=\"1429594500000\" seconds=\"387\" minutes=\"6\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"803_0_var0\" vehicle=\"333\" block=\"333\" tripTag=\"38125242\" />\n" +
            "  <prediction epochTime=\"1429595700000\" seconds=\"1587\" minutes=\"26\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"803_0_var0\" vehicle=\"334\" block=\"334\" tripTag=\"38125238\" />\n" +
            "  </direction>\n" +
            "</predictions>\n" +
            "<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Green Line\" routeTag=\"803\" stopTitle=\"Redondo Beach Station\" stopTag=\"80301_1\">\n" +
            "  <direction title=\"803 Redondo Beach Station\">\n" +
            "  <prediction epochTime=\"1429595249762\" seconds=\"1137\" minutes=\"18\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"803_1_var0\" vehicle=\"334\" block=\"334\" tripTag=\"38125344\" />\n" +
            "  </direction>\n" +
            "</predictions>\n" +
            "</body>";

    // Response 0 is 4 trips, blue+expo in/out each.
    // Response 1 adds 2 bogus trips as an update, so you can check that more trips are generated.
    public static final String BLUE_EXPO_7TH_METRO_STOPID = "80122";
    public static final String BLUE_EXPO_7TH_METRO_REQUEST = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=lametro-rail&stopId=80122";
    public static final String BLUE_EXPO_7TH_METRO_RESPONSE_0 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<body copyright=\"All data copyright Los Angeles Rail 2015.\">\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"806 Culver City Station\">\r\n  <prediction epochTime=\"1425851160000\" seconds=\"117\" minutes=\"1\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"108\" block=\"108\" tripTag=\"37870355\" />\r\n  <prediction epochTime=\"1425852060000\" seconds=\"1017\" minutes=\"16\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870356\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"806 7th Street / Metro Center Station\">\r\n  <prediction epochTime=\"1425851705289\" seconds=\"662\" minutes=\"11\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870411\" />\r\n  <prediction epochTime=\"1425852605289\" seconds=\"1562\" minutes=\"26\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"105\" block=\"105\" tripTag=\"37870412\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"801 Artesia Station\">\r\n  <prediction epochTime=\"1425851700000\" seconds=\"657\" minutes=\"10\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"111\" block=\"111\" tripTag=\"37870282\" />\r\n  <prediction epochTime=\"1425852600000\" seconds=\"1557\" minutes=\"25\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870283\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"801 7th Street / Metro Center Station\">\r\n  <prediction epochTime=\"1425851957349\" seconds=\"914\" minutes=\"15\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"801_0_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870245\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n</body>";
    public static final String BLUE_EXPO_7TH_METRO_RESPONSE_1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<body copyright=\"All data copyright Los Angeles Rail 2015.\">\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"806 Culver City Station\">\r\n  <prediction epochTime=\"1425851160000\" seconds=\"117\" minutes=\"1\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"108\" block=\"108\" tripTag=\"37870355\" />\r\n  <prediction epochTime=\"1425852060000\" seconds=\"1017\" minutes=\"16\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870356\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"808 Fake-o Square Station\">\r\n  <prediction epochTime=\"1425851705289\" seconds=\"662\" minutes=\"11\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870411\" />\r\n  <prediction epochTime=\"1425852605289\" seconds=\"1562\" minutes=\"26\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"105\" block=\"105\" tripTag=\"37870412\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"801 Artesia Station\">\r\n  <prediction epochTime=\"1425851700000\" seconds=\"657\" minutes=\"10\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"111\" block=\"111\" tripTag=\"37870282\" />\r\n  <prediction epochTime=\"1425852600000\" seconds=\"1557\" minutes=\"25\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870283\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"801 Bogus Square Station\">\r\n  <prediction epochTime=\"1425851957349\" seconds=\"914\" minutes=\"15\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"801_0_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870245\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n</body>";
}
