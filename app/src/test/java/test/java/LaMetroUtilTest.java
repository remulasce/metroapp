package test.java;

import com.remulasce.lametroapp.LaMetroUtil;
import com.remulasce.lametroapp.dynamic_data.types.Arrival;

import junit.framework.TestCase;

import java.util.Collection;

/* Created to support moving from XmlPullParser to something platform-independent

 */
public class LaMetroUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testParseAllArrivals() throws Exception {

        Collection<Arrival> arrivals = LaMetroUtil.parseAllArrivals(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE_0);

        assert(arrivals.size() == 7);
    }
}