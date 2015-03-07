package com.remulasce.lametroapp.dynamic_data;

import com.remulasce.lametroapp.components.network_status.NetworkStatusReporter;

/**
 * Created by Remulasce on 3/7/2015.
 */
public abstract class HTTPGetter {
    public static String getHTTPResponse(String message, NetworkStatusReporter reporter) {
        if (getter != null) {
            return getter.doGetHTTPResponse(message, reporter);
        }

        return "Needs Platform-Specific Implementation";
    }


    public abstract String doGetHTTPResponse(String message, NetworkStatusReporter reporter);

    private static HTTPGetter getter;
    public static void setHTTPGetter(HTTPGetter set) {
        getter = set;
    }
}
