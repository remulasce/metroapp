package test.java;

import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;

import junit.framework.TestCase;

// It's funny that I had tests at all ever.
public class HTTPGetterTest extends TestCase {


    // This test is funny because it relies upon the Nextrip server to be up.
    // Late-night, the 'predictions' tag should still appear. NexTrip tries to give
    // you empty predictions just so they include the destination names.
    public void testRealNetworkResponse() {
        HTTPGetter h = new HTTPGetter();

        String response = h.doGetHTTPResponse(TestConstants.BLUE_EXPO_7TH_METRO_REQUEST, null);

        assertTrue("HTTPGetter should have received some response from the NexTrip server",
                response.contains("<predictions agencyTitle="));
    }

}