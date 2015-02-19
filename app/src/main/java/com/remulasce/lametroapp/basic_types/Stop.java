package com.remulasce.lametroapp.basic_types;

import java.io.Serializable;

import android.util.Log;

import com.remulasce.lametroapp.LaMetroUtil;

public class Stop implements Serializable {
    private static final long serialVersionUID = 4336735625715608793L;

    private String raw = "";
    private String stopName = "";

    private BasicLocation location;

    public Stop() {}

    public Stop( String stopText ) {
        raw = stopText;
    }

    public Stop( int stopNum ) {
        raw = String.valueOf( stopNum );
    }

    public boolean isValid() {
        return LaMetroUtil.isValidStop( raw );
    }

    public int getNum() {
        return Integer.valueOf( raw );
    }
    public String getStopID() {
        return raw;
    }

    public String getString() {
        return raw;
    }

    public void setStopName( String stopName ) {
        if ( stopName != null && !stopName.isEmpty() ) {
            this.stopName = stopName;
        }
        else {
            Log.w( "Stop", "Bad stopname ignored" );
        }
    }

    public String getStopName() {
        if ( stopName.isEmpty() ) {
            return getString();
        }
        else {
            return stopName;
        }
    }

    public void setLocation(BasicLocation location) {
        this.location = location;
    }

    public BasicLocation getLocation() {
        return location;
    }


    // Ugh.
    public int hashCode() {
        return raw.hashCode();
    }
    public boolean equals( Object o ) {
        if ( o.getClass() != this.getClass()) { return false; }

        return (o.hashCode() == this.hashCode());
    }
}
