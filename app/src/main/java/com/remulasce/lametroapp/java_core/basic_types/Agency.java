package com.remulasce.lametroapp.java_core.basic_types;

/**
 * Created by Remulasce on 5/4/2015.
 *
 * An Agency is exactly what you'd give NexTrip in the Agency field.
 * One Agency per Stop (StopID).
 *
 * Note: the Los Angeles MTA has 2 agencies: lametro and lametro-rail.
 * One is busses, the other trains.
 */
public class Agency {
    public String raw = "";

    public Agency(String raw) {
        this.raw = raw;
    }

    // Quick and dirty check if we've ever actually been set.
    public boolean isValid() {
        return raw != null && !raw.isEmpty();
    }
}
