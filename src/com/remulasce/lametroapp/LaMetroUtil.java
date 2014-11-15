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

    public static boolean isValidStop( Stop stop ) {
        // I don't really know how to define this.
        return stop != null && stop.isValid();
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

        String URI = NEXTBUS_FEED_URL + "?command=predictions&a=" + agency + "&stopId=" + stop;

        if ( isValidRoute( route ) ) {
            URI += "&routeTag=" + route;
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

            String curDestination = "";
            String curRoute = "";
            String curStop = "";

            while ( eventType != XmlPullParser.END_DOCUMENT ) {
                if ( eventType == XmlPullParser.START_DOCUMENT ) {} else if ( eventType == XmlPullParser.END_DOCUMENT ) {} else if ( eventType == XmlPullParser.START_TAG ) {
                    String name = xpp.getName();

                    if ( name.equals( "predictions" ) ) {
                        curStop = xpp.getAttributeValue( null, "stopTag" );
                    }
                    if ( name.equals( "predictions" ) ) {
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
                            int indexOf_ = curStop.indexOf( '_' );
                            if ( indexOf_ > 0 ) {
                                curStop = curStop.substring( 0, curStop.indexOf( '_' ) );
                            }

                            Destination d = new Destination( curDestination );
                            Route r = new Route( curRoute );
                            Stop s = new Stop( curStop );
                            Vehicle v = new Vehicle( vehicleNum );

                            a.setDestination( d );
                            a.setRoute( r );
                            a.setStopID( s );
                            a.setEstimatedArrivalSeconds( seconds );
                            a.setVehicle( v );

                            ret.add( a );
                        }
                    }
                } else if ( eventType == XmlPullParser.END_TAG ) {
                    // System.out.println("End tag "+xpp.getName());
                } else if ( eventType == XmlPullParser.TEXT ) {
                    // System.out.println("Text "+xpp.getText());
                }
                eventType = xpp.next();
            }
        } catch ( XmlPullParserException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
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
                if ( eventType == XmlPullParser.START_DOCUMENT ) {
                    System.out.println( "Start document" );
                } else if ( eventType == XmlPullParser.END_DOCUMENT ) {
                    System.out.println( "End document" );
                } else if ( eventType == XmlPullParser.START_TAG ) {
                    String name = xpp.getName();
                    System.out.println( "Start tag " + name );

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
                } else if ( eventType == XmlPullParser.END_TAG ) {
                    System.out.println( "End tag " + xpp.getName() );
                } else if ( eventType == XmlPullParser.TEXT ) {
                    System.out.println( "Text " + xpp.getText() );
                }
                eventType = xpp.next();
            }

            if ( time != -1 ) {
                arrival.setEstimatedArrivalSeconds( time );
                arrival.setDestination( new Destination( shortDir ) );
            }

        } catch ( XmlPullParserException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String secondsToDisplay( int seconds ) {
        if ( seconds > 60 ) {
            return String.valueOf( seconds / 60 ) + " min";
        }
        if ( seconds > 1 ) {
            return String.valueOf( seconds ) + "s";
        }
        if ( seconds == 0 ) {
            return "1s";
        }
        return "error";
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
