package test.java;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.java_core.dynamic_data.types.StopRouteDestinationPrediction;

import junit.framework.TestCase;

/**
 * Created by Remulasce on 4/20/2015.
 */
public class StopRouteDestinationPredictionTest extends TestCase {

    StopRouteDestinationPrediction p;
    Stop s;
    Route r;

    public void setUp() throws Exception {
        super.setUp();

        // Redondo Beach Station
        // Green line [Redondo Beach Station, Norwalk Station]
        s = new Stop(80301);
        p = new StopRouteDestinationPrediction(s, r);

        // Don't try to get the actual network.
//        p.startPredicting();
        p.setTestMode();
    }

    public void tearDown() throws Exception {

    }

    public void testStartupState() {
        assertEquals("Prediction should be paused on startup", p.getPredictionState(), Prediction.PredictionState.PAUSED);
    }

    public void testFetchingState() {
        assert(p.getPredictionState() == Prediction.PredictionState.PAUSED);

        p.setGettingUpdate();

        assertEquals("Prediction should think it's going to network to get a state",
                p.getPredictionState(), Prediction.PredictionState.FETCHING);
    }

    public void testStartNArrivals() {
        // Should be 1 for all arrivals to Norwalk, and another for all Red Sta arrivals.
        p.handleResponse(TestConstants.GREEN_REDONDO_BEACH_RESPONSE_0);

        assertEquals("P should have 2 arrivals, one for all Norwalk and 1 for all Redondo Beach",
                p.getArrivals().size(), 2);
        assertEquals("P should know its arrivals are correct",
                p.getPredictionState(), Prediction.PredictionState.GOOD);
    }

    public void testStartNoNetwork() {
        p.handleResponse("");

        assertEquals("P should know it encountered an error",
                p.getPredictionState(), Prediction.PredictionState.BAD);
    }

    // We already have some arrivals, but then get a valid network update with 0 arrivals.
    // Strategy is to continue with the existing arrivals as 'good'
    public void testExist0Arrivals() {
        p.handleResponse(TestConstants.GREEN_REDONDO_BEACH_RESPONSE_0);
//        p.ha we don't have the proper test case yet
    }

    // We have some arrivals, then receive error.
    // Continue as 'cached', since the arrivals we received are still probably valid.
    // This happens when going underground, etc.
    public void testCached() {
        p.handleResponse(TestConstants.GREEN_REDONDO_BEACH_RESPONSE_0);
        assertTrue(p.getPredictionState() == Prediction.PredictionState.GOOD);

        p.handleResponse("");
        assertEquals("p should know it has only cached predictions now",
                p.getPredictionState(), Prediction.PredictionState.CACHED);
    }
}