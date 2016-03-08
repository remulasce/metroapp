package com.remulasce.lametroapp.java_core;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.Vehicle;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.java_core.static_data.RouteColorer;
import com.remulasce.lametroapp.java_core.static_data.StopLocationTranslator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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


// OK, I'm modifying this to do the correct thing if we ask it to handle GTFS instead of nextbus
// GTFS conveniently provides us with a tag called <uri></uri> that will let us know if it's GTFS or not, as well as the request URL

public class LaMetroUtil {
    private static final String NEXTBUS_FEED_URL = "http://webservices.nextbus.com/service/publicXMLFeed";
    private static final String BART_FEED_URL = "http://api.bart.gov/api/etd.aspx";

    private static final String BART_API_KEY = "MW9S-E7SL-26DU-VV8V";
    public static final String TAG = "LaMetroUtil";
    // See, if we had code reviews, we wouldn't be able to just slip a hardcoded API key into the public repo.
    // Instead we'd have to spend several days coming up with proper opsec and key handling procedures, wasting valuable time.
    // And that's why we don't have code reviews.
    public static final String BAY_511_FEED_URL = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopCode.aspx?token=7a0b4b7b-6a70-46d7-85aa-8a202fc44471";

    public static StopLocationTranslator locationTranslator;
    public static RouteColorer routeColorer;


    public static boolean isValidStop( String stop ) {
        if ( stop == null ) {
            return false;
        }
        if ( stop.isEmpty() ) {
            return false;
        }

        return true;
    }

    public static boolean isValidRoute( Route route ) {
        if (route == null || !route.isValid()) {
            return false;
        }

        return true;
    }

    public static String makePredictionsRequest( Stop stop, Route route ) {
        Agency agency = stop.getAgency();

        if (agency == null || !agency.isValid()) {
            Log.e(TAG, "No agency attached to stop, old functions removed. Specify the agency as part of your stops.");
            return null;
        }

        // Kinda sketchy, should work
        String URI = new String();
        if (agency.raw.equals("BART")) {
            URI = BART_FEED_URL + "?cmd=etd&orig=" + stop.getString() + "&key=" + BART_API_KEY;
        } else if (agency.raw.equals("vta")) { // Uh oh.
            URI = BAY_511_FEED_URL + "&stopcode="+stop.getStopID();
        } else {
            URI = NEXTBUS_FEED_URL + "?command=predictions&a=" + agency.raw + "&stopId="
                    + stop.getString();
            if ( isValidRoute(route) ) {
                URI += "&routeTag=" + route.getString();
            }
        }

        Log.w(TAG,"Request string: " + URI);
        return URI;
    }

    // Returns null if there's errors.
    // Change: No longer fills in locations to the stops!
    // This avoids having to regionalize in here.
    // Instead, we are aiming to only produce the information that is actually contained in
    //    the xml feed.
    //
    // We should probably make a new data type that only can contain what the xml feed has,
    //    but for now reusing Arrival / Stop is just too convenient.
    //
    // Agency required to tell between Nextrip / Bart / Bay 511. Null assumes Nextrip.
    public static List< Arrival > parseAllArrivals( String response, Agency agency ) {
        if (response == null || response.isEmpty()) {
            Log.d(TAG, "Error in input given to parseAllArrivals, possible network failure");
            return null;
        }



        List< Arrival > ret = parseWithJavaLibs(response, agency);

        return ret;
    }

    private static List < Arrival > parseWithJavaLibs(String response, Agency agency) {
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

            NodeList errors = docEle.getElementsByTagName("Error");
            if (errors != null && errors.getLength() != 0) {
                Log.d(TAG, "NexTrip/GTFS returned an error");
                return null;
            }

            // Figure out if it's nextbus or GTFS
            NodeList uriTags = docEle.getElementsByTagName("uri");

            String directionAttribute = new String();
            String routeAttribute = new String();
            String stopIDAttribute = new String();
            String stopTitleAttribute = new String();
            String vehicleAttribute = new String();

            int seconds = 0;

            if (uriTags != null && uriTags.getLength() > 0 ) {
                //We've gotten GTFS data
                // Some/Most of this is basically code reuse, but I'll try to figure out a nice way to do this tomorrow when I'm not so tired.
                //

                Element station = (Element)docEle.getElementsByTagName("station").item(0);
                stopIDAttribute = station.getElementsByTagName("abbr").item(0).getTextContent();
                stopTitleAttribute = station.getElementsByTagName("name").item(0).getTextContent();

                NodeList predictions = docEle.getElementsByTagName("etd");
                if (predictions != null && predictions.getLength() > 0) {
                    for (int i = 0; i < predictions.getLength();i++) {
                        Element prediction = (Element)predictions.item(i);

                        NodeList directions = prediction.getElementsByTagName("destination");
                        if (directions != null && directions.getLength() > 0) {
                            Element direction = (Element)directions.item(0);
                            directionAttribute = direction.getTextContent();
                        }

                        NodeList arrivals = prediction.getElementsByTagName("estimate");
                        for (int j = 0; j<arrivals.getLength();j++) {
                            Element arrival = (Element)arrivals.item(j);
                            int minutes;
                            try {
                                minutes = Integer.parseInt(arrival.getElementsByTagName("minutes").item(0).getTextContent());
                            } catch (Exception e)
                            {
                                minutes = 0;
                            }
                            seconds = minutes * 60; // BART doesn't provide seconds
                            routeAttribute = prediction.getElementsByTagName("abbreviation").item(0).getTextContent();

                            // BART doesn't give us bus numbers, maybe we can use the length of the car to be helpful with a hack?
                            vehicleAttribute = prediction.getElementsByTagName("length").item(0).getTextContent();

                            Destination d   = new Destination(directionAttribute);
                            Route r         = new Route(routeAttribute);
                            Stop s          = new Stop(stopIDAttribute);
                            Vehicle v       = new Vehicle(vehicleAttribute);

                            s.setStopName(stopTitleAttribute);
                            addNewArrival(ret, seconds, d, r, s, null);
                        }
                    }
                }
            } else if (agency.raw.equals("vta")){
                // We need like a "type" field in the agency at some (later) point.
                // This format is Bay Area 511 transit. Another custom-rigged XML.
                NodeList routes = docEle.getElementsByTagName("RouteList").item(0).getChildNodes();
                if (routes != null && routes.getLength() > 0) {
                    for (int i = 0; i < routes.getLength(); i++) {
                        Element route = (Element) routes.item(i);

                        NodeList directions = route.getElementsByTagName("RouteDirectionList").item(0).getChildNodes();
                        for (int j = 0; j < directions.getLength(); j++) {
                            Element direction = (Element) directions.item(j);

                            NodeList stops = direction.getElementsByTagName("Stop");
                            for (int k = 0; k < stops.getLength(); k++) {
                                Element stop = (Element) stops.item(k);

                                NodeList arrivals = stop.getElementsByTagName("DepartureTime");
                                for (int l = 0; l < arrivals.getLength(); l++) {
                                    Element arrival = (Element) arrivals.item(l);

                                    seconds = Integer.parseInt(arrival.getFirstChild().getTextContent()) * 60;

                                    Destination d   = new Destination(direction.getAttribute("Name"));
                                    Route r         = new Route(route.getAttribute("Name"));
                                    Stop s          = new Stop(stopIDAttribute);
                                    // No bus #s here as well. Unfortunate.
                                    Vehicle v       = null;

                                    s.setStopName(stop.getAttribute("name"));
                                    addNewArrival(ret, seconds, d, r, s, v);
                                }
                            }
                        }
                    }
                }
            } else {
                //We've gotten NextBus Data
                NodeList predictions = docEle.getElementsByTagName("predictions");
                if (predictions != null && predictions.getLength() > 0) {
                    for (int i = 0; i < predictions.getLength(); i++) {
                        Element prediction = (Element) predictions.item(i);

                        NodeList directions = prediction.getElementsByTagName("direction");
                        for (int j = 0; j < directions.getLength(); j++) {
                            Element direction = (Element) directions.item(j);

                            NodeList arrivals = direction.getElementsByTagName("prediction");
                            for (int k = 0; k < arrivals.getLength(); k++) {
                                Element arrival = (Element) arrivals.item(k);

                                seconds = Integer.parseInt(arrival.getAttribute("seconds"));

                                directionAttribute = direction.getAttribute("title");
                                routeAttribute = prediction.getAttribute("routeTag");
                                //stopIDAttribute = prediction.getAttribute("stopID");
                                stopTitleAttribute = prediction.getAttribute("stopTitle");
                                vehicleAttribute = arrival.getAttribute("vehicle");

                                //stopIDAttribute = cleanupStopID(stopIDAttribute);

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
            Log.w(TAG, "Unaddressed exception! " + e.getMessage());
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
            // This has been changed!
            // MetroUtil shouldn't have to deal with regionalization or state of the rest of the app.
            // It should just be convenience methods.
            // To get the stop locations from here, MetroUtil would need to know what region the stop is.
            // But, this is called only from parseArrivals from the xml stream, which doesn't include
            // the agency.
            // We could provide it, but again, we shouldn't be requesting info in Util.
            // We should just parse the xml conveniently, and let the rest of the app deal with it.
//            s.setLocation(locationTranslator.getStopLocation(s));
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

    //  The nextbus return data doesn't properly note which agency the request was for
    // So once we figure it out, fill it all in for us.
    public static void fillinAgencyAndStopID(List<Arrival> arrivals, Agency agency, String stopID) {
        if (arrivals == null) {
            return;
        }
        for (Arrival a : arrivals) {
            if (a.getStop().getAgency() != null || a.getRoute().getAgency() != null) {
                Log.w(TAG, "fillinAgency tried to fill an agency that already existed");
                return;
            }

            a.getStop().setAgency(agency);
            a.getRoute().setAgency(agency);
            a.getStop().setStopID(stopID);
        }
    }
}
