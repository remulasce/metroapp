package test.java;

import com.remulasce.lametroapp.dynamic_data.types.MultiArrivalTrip;
import com.remulasce.lametroapp.dynamic_data.types.StopRouteDestinationArrival;

import junit.framework.TestCase;

import org.mockito.Mockito;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MultiArrivalTripTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    // When the trip is dismissed, the arrival should be dismissed too.
    public void testDismiss() throws Exception {

        StopRouteDestinationArrival a = Mockito.mock(StopRouteDestinationArrival.class);
        MultiArrivalTrip t = new MultiArrivalTrip(a);

        t.dismiss();

        Mockito.verify(a).setScope(false);
    }
}