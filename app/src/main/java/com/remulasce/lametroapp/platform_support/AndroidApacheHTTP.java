package com.remulasce.lametroapp.platform_support;

import com.remulasce.lametroapp.java_core.dynamic_data.HTTPGetter;
import com.remulasce.lametroapp.java_core.network_status.NetworkStatusReporter;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Remulasce on 3/6/2015.
 *
 * <p>Platform-independent something or other. Probably just remake your own kind of thing on your
 * branch.
 */
public class AndroidApacheHTTP extends HTTPGetter {

  @Override
  public String doGetHTTPResponse(String request, NetworkStatusReporter statusReporter) {
    StringBuilder builder = new StringBuilder();

    URL url;
    try {
      url = new URL(request);
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
      statusReporter.reportFailure();
      return builder.toString();
    }

    // Not necessarily HttpsURLConnection, because our ancient gov't backends don't support HTTPS.
    HttpURLConnection cxn = null;
    try {
      cxn = (HttpURLConnection) url.openConnection();
      InputStream content = cxn.getInputStream();

      String s = IOUtils.toString(content, StandardCharsets.UTF_8.name());
      return s;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      statusReporter.reportFailure();
    } catch (IOException e) {
      e.printStackTrace();
      statusReporter.reportFailure();
    } finally {
      if (cxn != null) {
        cxn.disconnect();
      }
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
