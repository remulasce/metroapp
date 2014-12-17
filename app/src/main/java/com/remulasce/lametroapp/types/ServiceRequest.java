package com.remulasce.lametroapp.types;

/**
 * Created by Remulasce on 12/16/2014.
 */
public class ServiceRequest {
    String raw = "ServiceRequest";
    boolean inScope = true;

    public ServiceRequest() {}
    public ServiceRequest(String s) {
        this.raw = s;
    }

    public void descope() {
        inScope = false;
    }

    public boolean isInScope() {
        return inScope;
    }

    public String toString() {
        return raw;
    }
}
