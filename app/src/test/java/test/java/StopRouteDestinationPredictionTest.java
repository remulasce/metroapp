package test.java;

import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
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
        
    }


    public void testHandleResponse() throws Exception {

    }
}