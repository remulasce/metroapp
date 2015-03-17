package test.java;

import com.remulasce.lametroapp.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;

import junit.framework.TestCase;

import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PredictionManagerTest extends TestCase {

    private HTTPGetter httpGetter;
    private PredictionManager predictionManager;

    protected void setUp() throws Exception {
        httpGetter = Mockito.mock(HTTPGetter.class);

        predictionManager = new PredictionManager();
    }

    public void testResumeFirst() throws Exception {
        predictionManager.resumeTracking();

        assertTrue(predictionManager.isRunning());
    }
    public void testPauseFirst() throws Exception {
        predictionManager.pauseTracking();

        assertTrue(!predictionManager.isRunning());
    }
    public void testStartupShutdown() {
        predictionManager.resumeTracking();
        predictionManager.pauseTracking();

        assertTrue(!predictionManager.isRunning());
    }

    public void testReceiveAnyUpdates() {
        HTTPGetter.setHTTPGetter(httpGetter);
        Prediction p = Mockito.mock(Prediction.class);


        when(httpGetter.doGetHTTPResponse(TestConstants.BLUE_EXPO_7TH_METRO_REQUEST, null)
        ).thenReturn(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE);

        when (p.getRequestString()).thenReturn("http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=lametro-rail&stopId=80122");
        when (p.getRequestedUpdateInterval()).thenReturn(0);
        when (p.getTimeSinceLastUpdate()).thenReturn((long) 10000);

        predictionManager.startTracking(p);
        predictionManager.resumeTracking();

        try {
            // Whee, threading!
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(p).handleResponse("\r\n<?xml version=\"1.0\" encoding=\"utf-8\" ?> \r\n<body copyright=\"All data copyright Los Angeles Rail 2015.\">\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"806 Culver City Station\">\r\n  <prediction epochTime=\"1425851160000\" seconds=\"117\" minutes=\"1\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"108\" block=\"108\" tripTag=\"37870355\" />\r\n  <prediction epochTime=\"1425852060000\" seconds=\"1017\" minutes=\"16\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"806_1_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870356\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Expo Line\" routeTag=\"806\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"806 7th Street / Metro Center Station\">\r\n  <prediction epochTime=\"1425851705289\" seconds=\"662\" minutes=\"11\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"103\" block=\"103\" tripTag=\"37870411\" />\r\n  <prediction epochTime=\"1425852605289\" seconds=\"1562\" minutes=\"26\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"806_0_var0\" vehicle=\"105\" block=\"105\" tripTag=\"37870412\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_1\">\r\n  <direction title=\"801 Artesia Station\">\r\n  <prediction epochTime=\"1425851700000\" seconds=\"657\" minutes=\"10\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"111\" block=\"111\" tripTag=\"37870282\" />\r\n  <prediction epochTime=\"1425852600000\" seconds=\"1557\" minutes=\"25\" isDeparture=\"true\" affectedByLayover=\"true\" dirTag=\"801_1_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870283\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n<predictions agencyTitle=\"Los Angeles Rail\" routeTitle=\"Metro Blue Line\" routeTag=\"801\" stopTitle=\"7th Street / Metro Center Station - Metro Blue &amp; Expo Lines\" stopTag=\"80122_0\">\r\n  <direction title=\"801 7th Street / Metro Center Station\">\r\n  <prediction epochTime=\"1425851957349\" seconds=\"914\" minutes=\"15\" isDeparture=\"false\" affectedByLayover=\"true\" dirTag=\"801_0_var6\" vehicle=\"101\" block=\"101\" tripTag=\"37870245\" />\r\n  </direction>\r\n<message text=\"This Wknd: Blue &amp; Expo Line every 15 min. Buses replace trains btwn Artesia-Wardlow. metro.net/bluelineupgrades\" priority=\"Normal\"/>\r\n</predictions>\r\n</body>");
    }
}