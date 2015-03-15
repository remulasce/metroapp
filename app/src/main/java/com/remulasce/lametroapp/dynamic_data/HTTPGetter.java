package com.remulasce.lametroapp.dynamic_data;

import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;

/**
 * Created by Remulasce on 3/7/2015.
 */
public class HTTPGetter {

    public static String getHTTPResponse(String message, NetworkStatusReporter reporter) {
        return getHTTPGetter().doGetHTTPResponse(message, reporter);
    }

    public String doGetHTTPResponse(String message, NetworkStatusReporter reporter) {
        return "Not Implemented";
    }

    private static HTTPGetter getter;
    public static void setHTTPGetter(HTTPGetter set) {
        getter = set;
    }
    public static HTTPGetter getHTTPGetter() {
        if (getter == null) {
            getter = new HTTPGetter();
        }
        return getter;
    }
}
