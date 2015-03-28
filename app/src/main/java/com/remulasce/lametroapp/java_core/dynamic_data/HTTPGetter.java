package com.remulasce.lametroapp.java_core.dynamic_data;

import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

/**
 * Created by Remulasce on 3/7/2015.
 */
public class HTTPGetter {

    public static String getHTTPResponse(String message, NetworkStatusReporter reporter) {
        return getHTTPGetter().doGetHTTPResponse(message, reporter);
    }

    public native String doGetHTTPResponse(String message, NetworkStatusReporter reporter)
    /*-[
        
         NSURLResponse *serverResponse = nil;
         NSError *httpError = nil;
         
         NSURL *url = [NSURL URLWithString:message];
         NSURLRequest *request = [NSURLRequest requestWithURL: url];
         
         NSData *serverData = [NSURLConnection sendSynchronousRequest:request returningResponse:&serverResponse error:&httpError];
         
         NSString* resultString = [[NSString alloc] initWithData:serverData encoding:NSASCIIStringEncoding];
         
         return resultString;
    ]-*/;


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
