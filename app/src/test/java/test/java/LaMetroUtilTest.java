package test.java;

import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;

import junit.framework.TestCase;

import java.util.Collection;

/* Created to support moving from XmlPullParser to something platform-independent

 */
public class LaMetroUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testParseAllArrivals() throws Exception {

        Collection<Arrival> arrivals = LaMetroUtil.parseAllArrivals(TestConstants.BLUE_EXPO_7TH_METRO_RESPONSE_0, new Agency("lametro-rail", "lametro-rail", null, null));

        assert(arrivals.size() == 7);
    }
}