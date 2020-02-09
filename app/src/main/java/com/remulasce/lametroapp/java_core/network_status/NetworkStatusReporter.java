package com.remulasce.lametroapp.java_core.network_status;

/**
 * Created by Remulasce on 3/4/2015.
 *
 * Tiny reporter class to let user know when bad network things have happened.
 * Also report getting update, mainly for debug view.
 */
public interface NetworkStatusReporter {
    void reportFailure();
}
