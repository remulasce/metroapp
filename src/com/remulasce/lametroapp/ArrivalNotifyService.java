package com.remulasce.lametroapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class ArrivalNotifyService extends Service {

	boolean run = true;

	int stopID;
	String agency;
	String routeName;
	
	int runNum = 0;
	//int lastMinutes = -1;
	
	long arrivalTime = 0;
	long arrivalUpdatedAt = 0;
	int lastDisplayedSeconds = 10000;
	
	
	String lastDestination = "";

	private Thread netThread;
	private Thread notificationThread;
	
	
	private Runnable waitTask = new Runnable () {

		@Override
		public void run() {
			Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());
			StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			
			toast("Arrival Notification Service Started");

			while (run) {


				
				final String response = getXMLArrivalString(stopID, agency);				
				final int seconds = getFirstArrivalTime(response);

				if (seconds != -1) {
					long lastArrivalTime = arrivalTime;
					arrivalTime = System.currentTimeMillis() + seconds * 1000;
					arrivalUpdatedAt = System.currentTimeMillis();
	 
					if (runNum == 0) {
						toast ("Next arrival: "+seconds+" seconds");
					}
				}
				runNum++;
				if (runNum == 100) run = false;
				
				
				try {
					Thread.sleep(15000 + seconds * 100);
				} catch (InterruptedException e) {}
			}

			toast ("Notification Service Ending");		

			h.post(new Runnable() {
				@Override
				public void run() {
					stopForeground(true);
				}
			});
			
		}
	};

	private Runnable notificationTask = new Runnable() {
		
		@Override
		public void run() {

			while (run) {
				updateNotificationText();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		}
		
	};
	
	public int onStartCommand(Intent intent, int flags, int startId) {

		agency = intent.getExtras().getString("Agency");
		stopID = intent.getExtras().getInt("StopID");
		routeName = intent.getExtras().getString("Route");

		
		updateNotificationText();
		
		
		run = true;
		
		netThread = new Thread(waitTask);
		netThread.start();
		
		notificationThread = new Thread(notificationTask);
		notificationThread.start();

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		run = false;
		stopForeground(true);
	}

	public void toast(final String msg) {
		Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());

		h.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ArrivalNotifyService.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void updateNotificationText() {
		Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());		
		
		h.post(new Runnable() {
			@Override
			public void run() {
				
				String msg1;
				String msg2;
				
				int secondsTillArrival = (int)(arrivalTime - System.currentTimeMillis()) / 1000;
				int minutesSinceEstimate = (int)(System.currentTimeMillis() - arrivalUpdatedAt) / 1000 / 60; 
				
				if (secondsTillArrival < 0) { return; }
				if (minutesSinceEstimate < 0 ) { return; }
				
				if (secondsTillArrival <= 90) {
					msg2 = "Next arrival: "+secondsTillArrival+" seconds";
				}
				else {
					msg2 = "Next arrival: "+(secondsTillArrival/60)+" minutes";
				}
					
				if (minutesSinceEstimate >= 1) { msg2 += " : "+minutesSinceEstimate; }
				msg2 += "\n" + lastDestination;
				if (routeName != null && !routeName.isEmpty()) {
					msg1 = "Waiting for line "+routeName;
				}
				else {
					msg1 = "Waiting at stop "+stopID;
				}

				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(ArrivalNotifyService.this)
				        .setSmallIcon(R.drawable.ic_launcher);
				
				NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
				bigTextStyle.setBigContentTitle(msg1);
				bigTextStyle.bigText(msg2);

				mBuilder.setStyle(bigTextStyle);

				if (secondsTillArrival <= 90 && lastDisplayedSeconds > 90) {
					mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
					toast("Next arrival: "+secondsTillArrival+" seconds");
					
					Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					v.vibrate(2000);
				}
				
				Intent resultIntent = new Intent(ArrivalNotifyService.this, MainActivity.class);

				TaskStackBuilder stackBuilder = TaskStackBuilder.create(ArrivalNotifyService.this);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				Notification n = mBuilder.build();
				startForeground(294, n);
				mNotificationManager.notify(294, n);	
				
				lastDisplayedSeconds = secondsTillArrival;
				
			}
		});
	}

	public int getFirstArrivalTime(String xml) {
		int time = -1;
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader (xml));
			int eventType = xpp.getEventType();
			String curDirection = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) {
					System.out.println("Start document");
				} else if(eventType == XmlPullParser.END_DOCUMENT) {
					System.out.println("End document");
				} else if(eventType == XmlPullParser.START_TAG) {
					String name = xpp.getName();
					System.out.println("Start tag "+name);
					
					if(name.equals("direction")) { curDirection = xpp.getAttributeValue(null, "title"); }
					if(name.equals( "prediction" )) {

						String timeString = xpp.getAttributeValue(null, "seconds");

						int predTime = Integer.valueOf(timeString); 
						if (predTime >= 0 && ( predTime < time || time < 0) )
						{
							time = predTime;
							lastDestination = curDirection;
						}
					}
				} else if(eventType == XmlPullParser.END_TAG) {
					System.out.println("End tag "+xpp.getName());
				} else if(eventType == XmlPullParser.TEXT) {
					System.out.println("Text "+xpp.getText());
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return time;
	}
	
	public String getXMLArrivalString( int stopID, String agency) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String URI = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a="+agency+"&stopId="+stopID;
		if (routeName != null && !routeName.isEmpty()) {
			URI += "&routeTag="+routeName;
		}
		//HttpGet httpGet = new HttpGet("https://bugzilla.mozilla.org/rest/bug?assigned_to=lhenry@mozilla.com");
		HttpGet httpGet = new HttpGet(URI);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("JSON", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}


}
