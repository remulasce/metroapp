package com.remulasce.lametroapp.java_core.dynamic_data;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/*
 * Created by Remulasce on 3/7/2015.
 */
public class HTTPGetter {
  private static final String TAG = "HTTPGetter";

  private String readStream(InputStream is) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.name()));
      return IOUtils.toString(br);
    } catch (IOException e) {
      return "";
    }
  }

  public String doGetHTTPResponse(String message, NetworkStatusReporter reporter) {
    try {
      URL url = new URL(message);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      String response;
      response = readStream(urlConnection.getInputStream());
      urlConnection.disconnect();
      Log.v(TAG, "HTTP Response: " + response);
      return response;
    } catch (IOException e) {
      return "";
    } catch (Exception e) {
      return null;
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
