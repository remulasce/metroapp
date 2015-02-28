package test.java;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import com.remulasce.lametroapp.dynamic_data.types.MultiArrivalTrip;
import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationArrival;

import org.mockito.Mockito;


public class MultiArrivalTripInstrumentationTest extends ActivityInstrumentationTestCase2 {

    Activity activity;
    Instrumentation instrumentation;

    public MultiArrivalTripInstrumentationTest(Class activityClass) {
        super(activityClass);
    }


    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
        instrumentation = getInstrumentation();

        setActivityInitialTouchMode(true);
    }

    public void tearDown() throws Exception {

    }

    public void testConfirmation() throws Exception {
        StopRouteDestinationArrival a = Mockito.mock(StopRouteDestinationArrival.class);
        MultiArrivalTrip t = new MultiArrivalTrip(a);

        t.executeAction(activity);
    }
}