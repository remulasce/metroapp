package com.remulasce.lametroapp.platform_support;

import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;
import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Remulasce on 3/6/2015.
 *
 * Platform-independent something or other.
 * Probably just remake your own kind of thing on your branch.
 *
 */
public class AndroidApacheHTTP extends HTTPGetter{

    @Override
    public String doGetHTTPResponse(String request, NetworkStatusReporter statusReporter) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        String URI = request;

        HttpGet httpGet = new HttpGet(URI);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                if (statusReporter != null) {
                    statusReporter.reportFailure();
                }
            }
        } catch (ClientProtocolException e) {
            if (statusReporter != null) {
                statusReporter.reportFailure();
            }
            e.printStackTrace();
        } catch (IOException e) {
            if (statusReporter != null) {
                statusReporter.reportFailure();
            }
            e.printStackTrace();
        }
        return builder.toString();
    }


    // Singleton.
    private static AndroidApacheHTTP network;
    public static AndroidApacheHTTP getNetwork() {
        if (network == null) {
            network = new AndroidApacheHTTP();
        }

        return network;
    }
}
