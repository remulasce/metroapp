package test.java;

import com.remulasce.lametroapp.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;
import com.remulasce.lametroapp.dynamic_data.types.PredictionUpdateCallback;

import junit.framework.TestCase;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class PredictionManagerTest extends TestCase {

    private HTTPGetter network;
    private PredictionManager predictionManager;

    protected void setUp() throws Exception {
        network = Mockito.mock(HTTPGetter.class);

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
        HTTPGetter.setHTTPGetter(network);

        Prediction p = Mockito.mock(Prediction.class);
        //when(p.getRequestString())

    }
}