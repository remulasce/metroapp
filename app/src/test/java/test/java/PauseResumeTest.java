package test.java;

import com.remulasce.lametroapp.ServiceRequestHandler;
import com.remulasce.lametroapp.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;

import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Remulasce on 3/18/2015.
 *
 * Tests that things work properly when you close/reopen the app.
 * eg. when you close, there should be nothing updating, etc.
 *
 * We don't have a platform-independent serializer, so we'll just hold on to a request and readd
 * it to test out resuming with serialized objects.
 *
 */
public class PauseResumeTest extends TestCase {

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
    }
    private void stop() {
        serviceRequestHandler.StopPopulating();
        predictionManager.pauseTracking();
    }


    private void assertCoreStarted() {
        assertTrue("ServiceRequestHandler should be started", serviceRequestHandler.isRunning());
        assertTrue("PredictionManager should have started", predictionManager.isRunning());

        assertTrue("ServiceRequestHandler shouldn't have any requests yet", serviceRequestHandler.numRequests() == 0);
        assertTrue("PredictionManager shouldn't have any requests yet", predictionManager.numPredictions() == 0);
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

        r.startRequest();

        Thread.sleep(100);

        assertTrue("PredictionManager should be tracking 1 prediction", predictionManager.numPredictions() == 1);
        assertTrue("Request should have received 3 trips: 7th * 2, Culver, Long Beach", r.getTrips().size() == 4);
    }

    // We add a request, check it's running. Then we shutdown the entire system as though the activity was ending.
    // Then restart, adding the request to pretend like it had been serialized.
    public void testShutdown() throws Exception {
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

        r.startRequest();

        Thread.sleep(100);

        assertTrue("PredictionManager should be tracking 1 prediction", predictionManager.numPredictions() == 1);
        assertTrue("Request should have received 3 trips: 7th * 2, Culver, Long Beach", r.getTrips().size() == 4);


        serviceRequestHandler.StopPopulating();

        assertTrue("No requests should be predicting after cancel", predictionManager.numPredictions() == 0);
        // RequestHandler is allowed to hold onto requests, which will just get overwritten. More important is that these are paused.
//        assertTrue("RequestHandler shouldn't be tracking anything after stop", serviceRequestHandler.numRequests() == 0);

        for (ServiceRequest each : serviceRequestHandler.getRequests()) {
            assertTrue("All requests should have been paused", each.getLifecycleState() == ServiceRequest.RequestLifecycleState.PAUSED);
        }
        assertTrue(r.getLifecycleState() == ServiceRequest.RequestLifecycleState.PAUSED);

        stop();
        serviceRequestHandler = new ServiceRequestHandler();

        //So we pretend we've totally stopped the app, and now resumed it.
        // Add extra destination to test that an update was received.
        HTTPGetter newGetter = Mockito.mock(HTTPGetter.class);
        when(newGetter.doGetHTTPResponse(TestConstants.BLUE_EXPO_7TH_METRO_REQUEST, null))
                .thenReturn(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE_1);
        HTTPGetter.setHTTPGetter(newGetter);

        predictionManager = new PredictionManager();
        PredictionManager.setPredictionManager(predictionManager);
        predictionManager.rawSetNetwork(newGetter);


        // We haven't added anything, and are using new handlers. So they shouldn't be tracking yet.
        assertTrue("No requests should be predicting after cancel", predictionManager.numPredictions() == 0);
        assertTrue("RequestHandler shouldn't be tracking anything after restart", serviceRequestHandler.numRequests() == 0);

        addRequest(r);
        start();
        Thread.sleep(6000);

        verify(newGetter).doGetHTTPResponse(TestConstants.BLUE_EXPO_7TH_METRO_REQUEST, null);

        assertTrue(predictionManager.numPredictions() == 1);
        assertTrue(r.getLifecycleState() == ServiceRequest.RequestLifecycleState.RUNNING);
        assertTrue("RequestHandler should be tracking again after restart", serviceRequestHandler.numRequests() == 1);

        assertTrue("Request should have received 6 trips: 2 * bogus, 7th * 2, Culver, Long Beach", r.getTrips().size() == 6);
    }


}
