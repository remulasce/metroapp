package com.remulasce.lametroapp.java_core.dynamic_data;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Created by Remulasce on 3/7/2015.
 */
public class HTTPGetter {
  private static final String TAG = "HTTPGetter";

  private String readStream(InputStream is) {
    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      int i = is.read();
      while (i != -1) {
        bo.write(i);
        i = is.read();
      }
      return bo.toString();
    } catch (IOException e) {
      return "";
    }
  }

  public String doGetHTTPResponse(String message, NetworkStatusReporter reporter) {
    try {
      URL url = new URL(message);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      String response;
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());

      response = readStream(in);
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
