package com.remulasce.lametroapp.components.location;

/**
 * Created by Remulasce on 1/30/2015.
 *
 * Ugh. Singletons.
 */
public class GlobalLocationProvider {

    private static LocationRetriever retriever = null;

    public static void setRetriever( LocationRetriever r) {
        retriever = r;
    }

    public static LocationRetriever getRetriever() {
        return retriever;
    }
}
