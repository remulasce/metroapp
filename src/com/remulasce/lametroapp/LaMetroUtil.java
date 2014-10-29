package com.remulasce.lametroapp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.remulasce.lametroapp.pred.Arrival;

public class LaMetroUtil {
	public static final String NEXTBUS_FEED_URL = "http://webservices.nextbus.com/service/publicXMLFeed";


	public static boolean isValidStop( String stop ) {
		// I don't really know how to define this.
		return stop != null && !stop.isEmpty();
	}
	public static boolean isValidRoute( String route ) {
		if (route == null || route.isEmpty()) return false;
		try {
			int routeNum = Integer.valueOf(route);
			return routeNum > 0 && routeNum < 1000;
		} catch (Exception e){
			return false;
		}
	}

	public static String makePredictionsRequest( String stopID, String routeName ) {
		String agency = getAgencyFromRoute( routeName, Integer.valueOf( stopID ) );

		String URI = NEXTBUS_FEED_URL + "?command=predictions&a="+agency+"&stopId="+stopID;

		if ( isValidRoute( routeName ) ) {
			URI += "&routeTag="+routeName;
		}

		return URI;
	}

	public static void parsePredictionsResponse( Arrival arrival, String response ) {

		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader (response));
			int eventType = xpp.getEventType();
			
			String curDirection = "";
			int time = -1;
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) {
					System.out.println("Start document");
				} else if(eventType == XmlPullParser.END_DOCUMENT) {
					System.out.println("End document");
				} else if(eventType == XmlPullParser.START_TAG) {
					String name = xpp.getName();
					System.out.println("Start tag "+name);

					
					if(name.equals( "direction" ) ) { curDirection = xpp.getAttributeValue( null, "title" ); }
					if(name.equals( "prediction" ) ) {

						if ( curDirection == arrival.getDirection() )
						{
							String timeString = xpp.getAttributeValue( null, "seconds" );
	
							int predTime = Integer.valueOf( timeString ); 
							if ( predTime >= 0 && ( predTime < time || time < 0 ) )
							{
								time = predTime;
							}
						}
					}
				} else if(eventType == XmlPullParser.END_TAG) {
					System.out.println("End tag "+xpp.getName());
				} else if(eventType == XmlPullParser.TEXT) {
					System.out.println("Text "+xpp.getText());
				}
				eventType = xpp.next();
			}
			
		if ( time != -1 ) {
			arrival.setEstimatedArrivalSeconds(time);
		}
		
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getAgencyFromRoute(String routeName, int stopId) {
		if (routeName == null || routeName.isEmpty()) {
			if (stopId > 80000 && stopId < 81000) {
				return "lametro-rail";
			}

			return "lametro";
		}
		int route = Integer.valueOf(routeName);
		if ( route / 100 == 8 ) { return "lametro-rail"; }
		else if (route > 0 && route < 1000) { return "lametro"; }
		else {
			return "lametro";
		}
	}
}
