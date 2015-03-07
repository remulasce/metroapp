package test.java;

import com.remulasce.lametroapp.dynamic_data.PredictionManager;

import junit.framework.TestCase;

public class PredictionManagerTest extends TestCase {

    private PredictionManager predictionManager;

    protected void setUp() throws Exception {
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
}