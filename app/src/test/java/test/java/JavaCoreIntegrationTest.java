package test.java;

import com.remulasce.lametroapp.java_core.ServiceRequestHandler;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.java_core.dynamic_data.PredictionManager;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

/**
 * Created by Remulasce on 3/17/2015.
 *
 * This test is mainly to make sure the java_core section of the app doesn't have pesky references
 * to platform-specific code.
 *
 */
public class JavaCoreIntegrationTest extends TestCase {

    private ServiceRequestHandler serviceRequestHandler;
    private PredictionManager predictionManager;

    private HTTPGetter httpGetter;


    public void testSanity() {
        assert(true);
    }

    public void setUp() throws Exception{
        super.setUp();

        httpGetter = Mockito.mock(HTTPGetter.class);
        when(httpGetter.doGetHTTPResponse(TestConstants.BLUE_EXPO_7TH_METRO_REQUEST, null))
                .thenReturn(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE_0);
        HTTPGetter.setHTTPGetter(httpGetter);

        serviceRequestHandler = new ServiceRequestHandler();
        predictionManager = new PredictionManager();

        PredictionManager.setPredictionManager(predictionManager);
    }

    private void start() {
        serviceRequestHandler.StartPopulating();
        predictionManager.resumeTracking();
        predictionManager.setThrottle(false);
    }
    private void stop() {
        serviceRequestHandler.StopPopulating();
        predictionManager.pauseTracking();
    }

    public void testStartup() throws Exception {
        start();

        assertCoreStarted();
    }

    private void assertCoreStarted() {
        assertTrue("ServiceRequestHandler should be started", serviceRequestHandler.isRunning());
        assertTrue("PredictionManager should have started", predictionManager.isRunning());

        assertTrue("ServiceRequestHandler shouldn't have any requests yet", serviceRequestHandler.numRequests() == 0);
        assertTrue("PredictionManager shouldn't have any requests yet", predictionManager.numPredictions() == 0);
    }

    // This tests shutdown without it even starting.
    public void testShutdown() throws Exception {
        stop();

        assertFalse("ServiceRequestHandler should have quit", serviceRequestHandler.isRunning());
        assertFalse("PredictionManager should have quit", predictionManager.isRunning());
    }


    public void testStartupShutdown() throws Exception {
        testStartup();
        testShutdown();
    }


    public void testAddEmptyRequest() throws Exception {
        start();
        assertCoreStarted();

        ServiceRequest r = Mockito.mock(ServiceRequest.class);

        addRequest(r);

        assertTrue("There should be a request added", serviceRequestHandler.numRequests() == 1);
    }

    private void addRequest(ServiceRequest r) {
        ArrayList<ServiceRequest> requests = new ArrayList<ServiceRequest>();
        requests.add(r);

        serviceRequestHandler.SetServiceRequests(requests);
    }

    public void testAddStopServiceRequest() throws Exception {
        start();
        assertCoreStarted();

        Stop s = new Stop(TestConstants.BLUE_EXPO_7TH_METRO_STOPID);
        assertTrue("Stop should be valid", s.isValid());

        StopServiceRequest r = new StopServiceRequest(s, "Test7thMetroStop");
        assertTrue("ServiceRequest should be valid", r.isValid());
        assertTrue("ServiceRequest should be in scope", r.isInScope());

        addRequest(r);

        assertTrue("There should be a request added", serviceRequestHandler.numRequests() == 1);
        assertTrue(predictionManager.isRunning());

        // Side effect: Starts trip predicting.
        r.startRequest();

        Thread.sleep(100);

        assertTrue("PredictionManager should be tracking 1 prediction", predictionManager.numPredictions() == 1);
        assertTrue("Request should have received 4 trips: 7th * 2, Culver, Long Beach", r.getTrips().size() == 4);
    }
}
