package com.remulasce.lametroapp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import com.remulasce.lametroapp.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.Vehicle;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;

public class LaMetroUtil {
    private static final String NEXTBUS_FEED_URL = "http://webservices.nextbus.com/service/publicXMLFeed";

    public static StopLocationTranslator locationTranslator;

    public static boolean isValidStop( String stop ) {
        if ( stop == null ) {
            return false;
        }
        if ( stop.isEmpty() ) {
            return false;
        }

        try {
            int stopNum = Integer.parseInt( stop );

            if ( stopNum <= 0 ) {
                return false;
            }
            /* Actually, there are some ridiculous stopids
            if ( stopNum > 100000 ) {
                return false;
            }
            */
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public static boolean isValidRoute( Route route ) {
        if ( route == null || !route.isValid() )
            return false;
        try {
            int routeNum = Integer.valueOf( route.getString() );
            return routeNum > 0 && routeNum < 1000;
        } catch ( Exception e ) {
            return false;
        }
    }

    public static String makePredictionsRequest( Stop stop, Route route ) {
        String agency = getAgencyFromRoute( route, stop );

        String URI = NEXTBUS_FEED_URL + "?command=predictions&a=" + agency + "&stopId="
                + stop.getString();

        if ( isValidRoute( route ) ) {
            URI += "&routeTag=" + route.getString();
        }

        return URI;
    }

    public static List< Arrival > parseAllArrivals( String response ) {
        List< Arrival > ret = new ArrayList< Arrival >();

        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware( true );
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader( response ) );
            int eventType = xpp.getEventType();

            String curStopName = "";
            String curDestination = "";
            String curRoute = "";
            String curStopTag = "";

            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if ( eventType == XmlPullParser.START_DOCUMENT ) {} else if ( eventType == XmlPullParser.START_TAG ) {
                    String name = xpp.getName();

                    if ( name.equals( "predictions" ) ) {
                        curStopTag = xpp.getAttributeValue( null, "stopTag" );
                        curStopName = xpp.getAttributeValue( null, "stopTitle" );
                        curRoute = xpp.getAttributeValue( null, "routeTag" );
                    }
                    if ( name.equals( "direction" ) ) {
                        curDestination = xpp.getAttributeValue( null, "title" );
                    }
                    if ( name.equals( "prediction" ) ) {

                        Arrival a = new Arrival();

                        String vehicleNum = "";
                        int seconds = -1;

                        String timeString = xpp.getAttributeValue( null, "seconds" );
                        seconds = Integer.valueOf( timeString );

                        vehicleNum = xpp.getAttributeValue( null, "vehicle" );

                        boolean updated = false;
                        for ( Arrival aa : ret ) {
                            if ( aa.getDirection().equals( curDestination ) ) {
                                updated = true;
                                if ( aa.getEstimatedArrivalSeconds() > seconds ) {
                                    aa.setEstimatedArrivalSeconds( seconds );
                                }
                            }
                        }

                        if ( !updated ) {
                            int indexOf_ = curStopTag.indexOf( '_' );
                            if ( indexOf_ > 0 ) {
                                curStopTag = curStopTag.substring( 0, curStopTag.indexOf( '_' ) );
                            }

                            Destination d = new Destination( curDestination );
                            Route r = new Route( curRoute );
                            Stop s = new Stop( curStopTag );
                            s.setStopName( curStopName );
                            Vehicle v = new Vehicle( vehicleNum );

                            if (locationTranslator != null) {
                                s.setLocation(locationTranslator.getStopLocation(s));
                            }

                            a.setDestination( d );
                            a.setRoute( r );
                            a.setStop( s );
                            a.setEstimatedArrivalSeconds( seconds );
                            a.setVehicle( v );

                            ret.add( a );
                        }
                    }
                } else if ( eventType == XmlPullParser.END_TAG ) {} else if ( eventType == XmlPullParser.TEXT ) {}
                eventType = xpp.next();
            }
        } catch ( XmlPullParserException e1 ) {
            e1.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String timeToDisplay(int seconds) {
        if ( seconds > 60 ) {
            return "in " + standaloneTimeToDisplay(seconds);
        }
        if ( seconds > 1 ) {
            return "in " + standaloneTimeToDisplay(seconds);
        }
        if ( seconds == 0 ) {
            return "in " + standaloneTimeToDisplay(seconds);
        }
        return standaloneTimeToDisplay(seconds);
    }

    public static String standaloneTimeToDisplay(int seconds) {
        if ( seconds > 60 ) {
            return String.valueOf( seconds / 60 ) + " min";
        }
        if ( seconds > 1 ) {
            return String.valueOf( seconds ) + "s";
        }
        if ( seconds == 0 ) {
            return "1s";
        }
        return "arrived";
    }

    /** Returns the number of seconds to display as a subtext to the timeToDisplay methods.
     * Returns % 60 normally
     * Returns empty string if seconds <= 60, because timeToDisplay will display seconds remaining
     *  in the main display.
     */
    public static String standaloneSecondsRemainderTime(int seconds) {
        if (seconds <= 60) { return ""; }
        return (seconds % 60)+"s";
    }

    public static String getAgencyFromRoute( Route route, Stop stop )
            throws IllegalArgumentException {
        try {
            if ( route == null || !route.isValid() ) {
                if ( stop.getNum() > 80000 && stop.getNum() < 81000 ) {
                    return "lametro-rail";
                }

                return "lametro";
            }
            int routeN = Integer.valueOf( route.getString() );
            if ( routeN / 100 == 8 ) {
                return "lametro-rail";
            }
            else if ( routeN > 0 && routeN < 1000 ) {
                return "lametro";
            }
            else {
                return "lametro";
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( e.getLocalizedMessage() );
        }

    }
}
