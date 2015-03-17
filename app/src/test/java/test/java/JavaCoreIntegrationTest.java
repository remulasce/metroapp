package test.java;

import com.remulasce.lametroapp.ServiceRequestHandler;
import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;

import junit.framework.TestCase;

import org.mockito.Mockito;

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
                .thenReturn(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE);

        serviceRequestHandler = new ServiceRequestHandler();
        predictionManager = new PredictionManager();
    }

    private void start() {
        serviceRequestHandler.StartPopulating();
        predictionManager.resumeTracking();
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


    public void testAddRequest() throws Exception {
        start();
        assertCoreStarted();

        //ServiceRequest r = new ServiceRequest();
    }
}
