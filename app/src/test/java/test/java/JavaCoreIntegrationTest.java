package test.java;

import com.remulasce.lametroapp.ServiceRequestHandler;
import com.remulasce.lametroapp.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.dynamic_data.PredictionManager;

import junit.framework.TestCase;

import org.mockito.Mockito;

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


        serviceRequestHandler = new ServiceRequestHandler();
        predictionManager = new PredictionManager();
    }

    public void testStartup() throws Exception {

        serviceRequestHandler.StartPopulating();
        predictionManager.resumeTracking();
    }

}
