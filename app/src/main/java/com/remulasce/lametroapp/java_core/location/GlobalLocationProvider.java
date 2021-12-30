package com.remulasce.lametroapp.java_core.location;

/**
 * Created by Remulasce on 1/30/2015.
 *
 * <p>Ugh. Singletons.
 */
public class GlobalLocationProvider {

  private static LocationRetriever retriever = null;

  public static void setRetriever(LocationRetriever r) {
    retriever = r;
  }

  public static LocationRetriever getRetriever() {
    return retriever;
  }
}
