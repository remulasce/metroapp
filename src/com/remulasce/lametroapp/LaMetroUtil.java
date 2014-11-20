package com.remulasce.lametroapp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import types.Destination;
import types.Route;
import types.Stop;
import types.Vehicle;

import com.remulasce.lametroapp.pred.Arrival;

public class LaMetroUtil {
    public static final String NEXTBUS_FEED_URL = "http://webservices.nextbus.com/service/publicXMLFeed";

    // TODO: Pull this from Metro data, not guesses.
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
            if ( stopNum > 100000 ) {
                return false;
            }
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
                if ( eventType == XmlPullParser.START_DOCUMENT ) {} else if ( eventType == XmlPullParser.END_DOCUMENT ) {} else if ( eventType == XmlPullParser.START_TAG ) {
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

    public static void parseFirstArrival( Arrival arrival, String response ) {

        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware( true );
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader( response ) );
            int eventType = xpp.getEventType();

            String curDirection = "";
            String shortDir = "";
            int time = -1;

            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if ( eventType == XmlPullParser.START_DOCUMENT ) {} else if ( eventType == XmlPullParser.END_DOCUMENT ) {} else if ( eventType == XmlPullParser.START_TAG ) {
                    String name = xpp.getName();

                    if ( name.equals( "direction" ) ) {
                        curDirection = xpp.getAttributeValue( null, "title" );
                    }
                    if ( name.equals( "prediction" ) ) {
                        String timeString = xpp.getAttributeValue( null, "seconds" );

                        int predTime = Integer.valueOf( timeString );
                        if ( predTime >= 0 && ( predTime < time || time < 0 ) )
                        {
                            time = predTime;
                            shortDir = curDirection;
                        }
                    }
                } else if ( eventType == XmlPullParser.END_TAG ) {} else if ( eventType == XmlPullParser.TEXT ) {}
                eventType = xpp.next();
            }

            if ( time != -1 ) {
                arrival.setEstimatedArrivalSeconds( time );
                arrival.setDestination( new Destination( shortDir ) );
            }

        } catch ( XmlPullParserException e1 ) {
            e1.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    public static String secondsToDisplay( int seconds ) {
        if ( seconds > 60 ) {
            return "in " + String.valueOf( seconds / 60 ) + " min";
        }
        if ( seconds > 1 ) {
            return "in " + String.valueOf( seconds ) + "s";
        }
        if ( seconds == 0 ) {
            return "in " + "1s";
        }
        return "arrived";
    }

    public static String getAgencyFromRoute( Route route, Stop stop ) {
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
    }
}
