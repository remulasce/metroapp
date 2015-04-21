package test.java;

import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Prediction;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Remulasce on 4/20/2015.
 */
public class StopServiceRequestTest extends TestCase {

    StopServiceRequest s;

    public void setUp() throws Exception {
        super.setUp();

        Stop stop = new Stop("80301");
        s = new StopServiceRequest(stop, "TestStopServiceRequest");
    }

    public void tearDown() throws Exception {

    }

    public void testSanity() {}

    // Using the dummy s stop, make sure we're not trying to show anything
    public void testEmptyPrediction() {
        assertEquals("s shouldn't be displaying any status",
                s.getTestStatusState(), StopServiceRequest.NetworkStatusState.NOTHING);
    }

    private void setPrediction(Prediction p) {
        ArrayList<Prediction> overridePredictions = new ArrayList<Prediction>();
        overridePredictions.add(p);
        s.testRawSetPredictions(overridePredictions);
    }

    private void setPredictions(Prediction[] p) {
        s.testRawSetPredictions(Arrays.asList(p));
    }

    public void testOneFetching() {
        setPrediction(new P(Prediction.PredictionState.FETCHING));

        assertEquals("Request should show status as fetching",
                s.getTestStatusState(), StopServiceRequest.NetworkStatusState.SPINNER);
    }



    private class P extends Prediction {
        private PredictionState state;
        private P(PredictionState state) {
            this.state = state;
        }

        @Override
        public void startPredicting() {

        }

        @Override
        public void stopPredicting() {

        }

        @Override
        public void restoreTrips() {

        }

        @Override
        public void cancelTrips() {

        }

        @Override
        public boolean hasAnyPredictions() {
            return false;
        }

        @Override
        public int getRequestedUpdateInterval() {
            return 0;
        }

        @Override
        public long getTimeSinceLastUpdate() {
            return 0;
        }

        @Override
        public PredictionState getPredictionState() {
            return state;
        }

        @Override
        public String getRequestString() {
            return null;
        }
    }

}