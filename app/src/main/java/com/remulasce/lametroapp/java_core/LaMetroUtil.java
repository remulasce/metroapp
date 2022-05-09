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
import com.remulasce.lametroapp.java_core.static_data.types.RouteColor;
import com.remulasce.lametroapp.static_data.hardcoded_hacks.HardcodedHacks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// OK, I'm modifying this to do the correct thing if we ask it to handle GTFS instead of nextbus
// GTFS conveniently provides us with a tag called <uri></uri> that will let us know if it's GTFS or
// not, as well as the request URL

public class LaMetroUtil {
  // Nextbus doesn't have a valid SSL certificate. Ugh.
  private static final String NEXTBUS_FEED_URL =
      "https://retro.umoiq.com/service/publicXMLFeed";

  // THANK YOU. BART supports HTTPS.
  private static final String BART_FEED_URL = "https://api.bart.gov/api/etd.aspx";

  private static final String BART_API_KEY = "MW9S-E7SL-26DU-VV8V";
  public static final String TAG = "LaMetroUtil";
  // See, if we had code reviews, we wouldn't be able to just slip a hardcoded API key into the
  // public repo.
  // Instead we'd have to spend several days coming up with proper opsec and key handling
  // procedures, wasting valuable time.
  // And that's why we don't have code reviews.
  // Also ugh, Bay 511 doesn't have a valid SSL cert.
  public static final String BAY_511_FEED_URL =
      "https://api.511.org/transit/stopmonitoring?api_key=f036cd72-4465-425d-9ce2-df2478c7f804&format=xml";

  public static StopLocationTranslator locationTranslator;
  public static RouteColorer routeColorer;

  public static boolean isValidStop(String stop) {
    if (stop == null) {
      return false;
    }
    if (stop.isEmpty()) {
      return false;
    }

    return true;
  }

  public static boolean isValidRoute(Route route) {
    if (route == null || !route.isValid()) {
      return false;
    }

    return true;
  }

  public static String makePredictionsRequest(Stop stop, Route route) {
    Agency agency = stop.getAgency();

    if (agency == null || !agency.isValid()) {
      Log.e(
          TAG,
          "No agency attached to stop, old functions removed. Specify the agency as part of your stops.");
      return null;
    }

    // Kinda sketchy, should work
    String URI = "";
    if (HardcodedHacks.useBart(agency)) {
      URI = BART_FEED_URL + "?cmd=etd&orig=" + stop.getString() + "&key=" + BART_API_KEY;
    } else if (HardcodedHacks.useBay511(agency)) {
      URI = BAY_511_FEED_URL + "&stopcode=" + stop.getStopID() + "&agency=" + agency.raw;
    } else if (HardcodedHacks.useNextrip(agency)) {
      URI =
          NEXTBUS_FEED_URL + "?command=predictions&a=" + agency.raw + "&stopId=" + stop.getString();
      if (isValidRoute(route)) {
        URI += "&routeTag=" + route.getString();
      }
    } else {
      Log.w(TAG, "Don't know how to get predictions for agency: " + agency.raw);
    }

    Log.d(TAG, "Request string: " + URI);
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
  public static List<Arrival> parseAllArrivals(String response, Agency agency) {
    if (response == null || response.isEmpty()) {
      Log.d(TAG, "Error in input given to parseAllArrivals, possible network failure");
      return null;
    }

    return parseWithJavaLibs(response, agency);
  }

  private static List<Arrival> parseWithJavaLibs(String response, Agency agency) {
    List<Arrival> ret = new ArrayList<Arrival>();
    // get the factory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {

      // Using factory get an instance of document builder
      DocumentBuilder db = dbf.newDocumentBuilder();

      // parse using builder to get DOM representation of the XML file
      InputSource is = new InputSource(new StringReader(response));
      is.setEncoding(StandardCharsets.UTF_8.displayName());

      Document dom = db.parse(is);

      // get the root element
      Element docEle = dom.getDocumentElement();

      NodeList errors = docEle.getElementsByTagName("Error");
      if (errors != null && errors.getLength() != 0) {
        Log.d(TAG, "NexTrip/GTFS returned an error");
        return null;
      }

      // Figure out if it's nextbus or GTFS
      NodeList uriTags = docEle.getElementsByTagName("uri");

      String directionAttribute = "";
      String routeAttribute;
      String stopIDAttribute = "";
      String stopTitleAttribute;
      String vehicleAttribute;

      int seconds = 0;

      if (HardcodedHacks.useBart(agency)) {
        // We've gotten GTFS data
        // Some/Most of this is basically code reuse, but I'll try to figure out a nice way to do
        // this tomorrow when I'm not so tired.
        //

        Element station = (Element) docEle.getElementsByTagName("station").item(0);
        stopIDAttribute = station.getElementsByTagName("abbr").item(0).getTextContent();
        stopTitleAttribute = station.getElementsByTagName("name").item(0).getTextContent();

        NodeList predictions = docEle.getElementsByTagName("etd");
        if (predictions != null && predictions.getLength() > 0) {
          for (int i = 0; i < predictions.getLength(); i++) {
            Element prediction = (Element) predictions.item(i);

            NodeList directions = prediction.getElementsByTagName("destination");
            if (directions != null && directions.getLength() > 0) {
              Element direction = (Element) directions.item(0);
              directionAttribute = direction.getTextContent();
            }

            NodeList arrivals = prediction.getElementsByTagName("estimate");
            for (int j = 0; j < arrivals.getLength(); j++) {
              Element arrival = (Element) arrivals.item(j);
              int minutes;
              try {
                minutes =
                    Integer.parseInt(
                        arrival.getElementsByTagName("minutes").item(0).getTextContent());
              } catch (Exception e) {
                minutes = 0;
              }
              seconds = minutes * 60; // BART doesn't provide seconds
              routeAttribute =
                  prediction.getElementsByTagName("abbreviation").item(0).getTextContent();

              // BART doesn't give us bus numbers, maybe we can use the length of the car to be
              // helpful with a hack?
              vehicleAttribute = prediction.getElementsByTagName("length").item(0).getTextContent();

              // Like "#ffff33'. Needed for Bart because they don't respect their gtfs route names.
              String hexColor =
                  prediction.getElementsByTagName("hexcolor").item(0).getTextContent();

              Destination d = new Destination(directionAttribute);
              Route r = new Route(routeAttribute);
              Stop s = new Stop(stopIDAttribute);
              Vehicle v = new Vehicle(vehicleAttribute);

              r.setAgency(agency);
              r.setColor(new RouteColor(hexColor));
              s.setStopName(stopTitleAttribute);
              addNewArrival(ret, seconds, d, r, s, null);
            }
          }
        }
      } else if (HardcodedHacks.useBay511(agency)) {
        // We need like a "type" field in the agency at some (later) point.
        // This format is Bay Area 511 transit. Another custom-rigged XML.
        NodeList routes = docEle.getElementsByTagName("RouteList").item(0).getChildNodes();
        if (routes != null && routes.getLength() > 0) {
          for (int i = 0; i < routes.getLength(); i++) {
            Element route = (Element) routes.item(i);

            NodeList directions =
                route.getElementsByTagName("RouteDirectionList").item(0).getChildNodes();
            for (int j = 0; j < directions.getLength(); j++) {
              Element direction = (Element) directions.item(j);

              NodeList stops = direction.getElementsByTagName("Stop");
              for (int k = 0; k < stops.getLength(); k++) {
                Element stop = (Element) stops.item(k);

                NodeList arrivals = stop.getElementsByTagName("DepartureTime");
                for (int l = 0; l < arrivals.getLength(); l++) {
                  Element arrival = (Element) arrivals.item(l);

                  seconds = Integer.parseInt(arrival.getFirstChild().getTextContent()) * 60;

                  Destination d = new Destination(direction.getAttribute("Name"));
                  Route r = new Route(route.getAttribute("Name"));
                  Stop s = new Stop(stopIDAttribute);
                  // No bus #s here as well. Unfortunate.
                  Vehicle v = null;

                  s.setStopName(stop.getAttribute("name"));
                  r.setAgency(agency);
                  addNewArrival(ret, seconds, d, r, s, v);
                }
              }
            }
          }
        }
      } else if (HardcodedHacks.useNextrip(agency)) {
        // We've gotten NextBus Data
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
                // stopIDAttribute = prediction.getAttribute("stopID");
                stopTitleAttribute = prediction.getAttribute("stopTitle");
                vehicleAttribute = arrival.getAttribute("vehicle");

                // stopIDAttribute = cleanupStopID(stopIDAttribute);

                Destination d = new Destination(directionAttribute);
                Route r = new Route(routeAttribute);
                Stop s = new Stop(stopIDAttribute);
                Vehicle v = new Vehicle(vehicleAttribute);

                r.setAgency(agency);
                s.setStopName(stopTitleAttribute);
                addNewArrival(ret, seconds, d, r, s, v);
              }
            }
          }
        }
      } else {
        Log.w(TAG, "Couldn't find an agency to parse prediction response for: " + agency);
      }
    } catch (ParserConfigurationException | SAXException | IOException pce) {
      pce.printStackTrace();
      return null;
    } catch (Exception e) {
      Log.w(TAG, "Unaddressed exception! " + e.getMessage());
      return null;
    }

    return ret;
  }

  private static void addNewArrival(
      List<Arrival> ret, int seconds, Destination d, Route r, Stop s, Vehicle v) {
    Log.v(TAG, "Adding new arrival " + seconds + " " + d + " " + r + " " + s + " " + v);

    if (routeColorer != null) {
      r.setColor(routeColorer.getColor(r));
    }

    Arrival a = new Arrival();

    a.setDestination(d);
    a.setRoute(r);
    a.setStop(s);
    a.setEstimatedArrivalSeconds(seconds);
    a.setVehicle(v);

    ret.add(a);
  }

  public static String timeToDisplay(int seconds) {
    if (seconds > 60) {
      return "in " + standaloneTimeToDisplay(seconds);
    }
    if (seconds > 1) {
      return "in " + standaloneTimeToDisplay(seconds);
    }
    if (seconds == 0) {
      return "in " + standaloneTimeToDisplay(seconds);
    }
    return standaloneTimeToDisplay(seconds);
  }

  public static String standaloneTimeToDisplay(int seconds) {
    if (seconds > 60) {
      return String.valueOf(seconds / 60) + " min";
    }
    if (seconds >= 1) {
      return String.valueOf(seconds) + "s";
    }
    return "arrived";
  }

  /**
   * Returns the number of seconds to display as a subtext to the timeToDisplay methods. Returns %
   * 60 normally Returns empty string if seconds <= 60, because timeToDisplay will display seconds
   * remaining in the main display.
   */
  public static String standaloneSecondsRemainderTime(int seconds) {
    if (seconds <= 60) {
      return "";
    }
    return (seconds % 60) + "s";
  }

  public static String convertMetersToDistanceDisplay(double distance) {
    double yards = distance * 1.09361;

    if (yards > 200) {
      double miles = distance * 0.000621371;

      if (miles < 10) {
        return (int) (miles * 10) / 10.0 + "mi";
      } else if (miles < 100) {
        return (int) (miles + 0.5) + "mi";
      } else {
        return "Far!";
      }
    } else {
      return (int) yards + "yd";
    }
  }

  //  The nextbus return data doesn't properly note which agency the request was for
  // So once we figure it out, fill it all in for us.
  public static void fillinAgencyAndStopID(List<Arrival> arrivals, Agency agency, String stopID) {
    if (arrivals == null) {
      return;
    }
    for (Arrival a : arrivals) {
      if (a.getStop().getAgency() != null) {
        Log.w(TAG, "fillinAgency tried to fill an agency that already existed");
      }

      a.getStop().setAgency(agency);
      a.getRoute().setAgency(agency);
      a.getStop().setStopID(stopID);
    }
  }
}
