package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.Vehicle;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.remulasce.lametroapp.java_core.RegionalizationHelper;

public class LaMetroUtil {
    private static final String NEXTBUS_FEED_URL = "http://webservices.nextbus.com/service/publicXMLFeed";
    public static final String TAG = "LaMetroUtil";

    public static StopLocationTranslator locationTranslator;
    public static RouteColorer routeColorer;


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

    // Returns null if there's errors.
    public static List< Arrival > parseAllArrivals( String response ) {
        List< Arrival > ret = parseWithJavaLibs(response);

        return ret;
    }

    private static List < Arrival > parseWithJavaLibs(String response) {
        List<Arrival> ret = new ArrayList<Arrival>();
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new InputSource( new StringReader(response)));

            //get the root element
            Element docEle = dom.getDocumentElement();

            NodeList predictions = docEle.getElementsByTagName("predictions");
            if(predictions != null && predictions.getLength() > 0) {
                for(int i = 0 ; i < predictions.getLength();i++) {
                    Element prediction = (Element)predictions.item(i);

                    NodeList directions = prediction.getElementsByTagName("direction");
                    for(int j = 0 ; j < directions.getLength(); j++) {
                        Element direction = (Element)directions.item(j);

                        NodeList arrivals = direction.getElementsByTagName("prediction");
                        for(int k = 0 ; k < arrivals.getLength(); k++) {
                            Element arrival = (Element)arrivals.item(k);

                            int seconds = Integer.parseInt(arrival.getAttribute("seconds"));

                            String directionAttribute = direction.getAttribute("title");
                            String routeAttribute = prediction.getAttribute("routeTag");
                            String stopIDAttribute = prediction.getAttribute("stopTag");
                            String stopTitleAttribute = prediction.getAttribute("stopTitle");
                            String vehicleAttribute = arrival.getAttribute("vehicle");

                            stopIDAttribute = cleanupStopID(stopIDAttribute);

                            Destination d   = new Destination(directionAttribute);
                            Route r         = new Route(routeAttribute);
                            Stop s          = new Stop(stopIDAttribute);
                            Vehicle v       = new Vehicle(vehicleAttribute);

                            s.setStopName(stopTitleAttribute);

                            addNewArrival(ret, seconds, d, r, s, v);
                        }
                    }
                }
            }


        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
            return null;
        }catch(SAXException se) {
            se.printStackTrace();
            return null;
        }catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }catch (Exception e) {
            Log.w(TAG, "Unaddressed exception!");
            return null;
        }

        return ret;
    }

    // Metro adds _etc to the end of stops sometimes. It's related to multiple entrances per station
    // or something. This gets rid of that.
    private static String cleanupStopID(String stopIDAttribute) {
        int indexOf_ = stopIDAttribute.indexOf( '_' );
        if ( indexOf_ > 0 ) {
            stopIDAttribute = stopIDAttribute.substring(0, stopIDAttribute.indexOf('_'));
        }
        return stopIDAttribute;
    }

    private static void parseWithAndroidLibs(String response, List<Arrival> ret) {
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
                        String vehicleNum;
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
                            curStopTag = cleanupStopID(curStopTag);

                            Destination d = new Destination( curDestination );
                            Route r = new Route( curRoute );
                            Stop s = new Stop( curStopTag );
                            s.setStopName( curStopName );
                            Vehicle v = new Vehicle( vehicleNum );

                            addNewArrival(ret, seconds, d, r, s, v);
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
    }

    private static void addNewArrival(List<Arrival> ret, int seconds, Destination d, Route r, Stop s, Vehicle v) {
        Log.v(TAG, "Adding new arrival "+seconds+" "+d+" "+r+" "+s+" "+v);

        if (locationTranslator != null) {
            s.setLocation(locationTranslator.getStopLocation(s));
        }

        if (routeColorer != null) {
            r.setColor(routeColorer.getColor(r));
        }

        Arrival a = new Arrival();

        a.setDestination(d);
        a.setRoute(r);
        a.setStop(s);
        a.setEstimatedArrivalSeconds( seconds );
        a.setVehicle(v);

        ret.add( a );
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
        if ( seconds >= 1 ) {
            return String.valueOf( seconds ) + "s";
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
            if  (RegionalizationHelper.getInstance().agencyName.equals("actransit"))
            {
                return "actransit";
            } else if (RegionalizationHelper.getInstance().agencyName.equals("lametro"))
            {
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
            else {
                return "lametro";
            }
        } catch ( Exception e ) {
            throw new IllegalArgumentException( e.getLocalizedMessage() );
        }

    }
}
