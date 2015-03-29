package com.remulasce.lametroapp.java_core.dynamic_data;

import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;
import java.lang.StringBuilder;
import java.net.*;
import java.io.*;

/*
 * Created by Remulasce on 3/7/2015.
 */
public class HTTPGetter {

    public static String getHTTPResponse(String message, NetworkStatusReporter reporter) {
        return getHTTPGetter().doGetHTTPResponse(message, reporter);
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    
    public String doGetHTTPResponse(String message, NetworkStatusReporter reporter)
    {
        try {
        URL url = new URL(message);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response;
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            
            response = readStream(in);
            urlConnection.disconnect();
            return response;
        }
        catch(IOException e)
        {
            return "";
        }
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
