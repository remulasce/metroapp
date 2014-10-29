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
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ArrivalNotifyService extends Service {

	boolean run = true;

	int stopID;
	String agency;
	String routeName;
	
	int runNum = 0;
	int lastMinutes = -1;

	private Thread taskThread;
	private Runnable waitTask = new Runnable () {

		@Override
		public void run() {
			Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());

			toast("Arrival Notification Service Started");

			while (run) {
				StrictMode.ThreadPolicy policy = new StrictMode.
						ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);


				//toast("Getting Predictions for "+routeName);
				
				final String response = getXMLArrivalString(stopID, agency);				
				final int minutes = getFirstArrivalTime(response);
				lastMinutes = minutes;
				updateNotificationText();
 
				if (runNum == 0 && minutes != 0) {
					toast ("Next arrival: "+minutes+" minutes");
				}
					 
				
				if (minutes == 0)
				{
					toast("Next arrival: "+minutes+" minutes");
					
					Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					v.vibrate(1000);
					
					run = false;
					break;
				}
				
				runNum++;
				if (runNum == 100) {
					Log.e("NotifyService", "Timed out after "+runNum+" checks, exiting");
					run = false;
				}
				
				try {
					Thread.sleep(10000);
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

	public int onStartCommand(Intent intent, int flags, int startId) {

		agency = intent.getExtras().getString("Agency");
		stopID = intent.getExtras().getInt("StopID");
		routeName = intent.getExtras().getString("Route");

		Log.d("NotifyService", "Started notification service for agency "+agency+", stopID "+stopID+", route "+routeName);
		
		updateNotificationText();
		
		runNum = 0;
		run = true;
		
		taskThread = new Thread(waitTask);
		taskThread.start();

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
				String msg2 = "Next arrival: "+lastMinutes+" minutes";
				if (routeName != null && !routeName.isEmpty()) {
					msg1 = "Waiting for line "+routeName;
				}
				else {
					msg1 = "Waiting at stop "+stopID;
				}
				
				/*
				//Define Notification Manager
				NotificationManager notificationManager = (NotificationManager) ArrivalNotifyService.this.getSystemService(Context.NOTIFICATION_SERVICE);

				//Define sound URI
				Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentTitle("Metro Arrival Alert")
				        .setContentText(msg1)
				        .setSubText(msg2)
				        .setSound(soundUri); //This sets the sound to play

				//Display notification
				notificationManager.notify(0, mBuilder.build());
				*/
				
				
				Notification notification = new Notification(R.drawable.ic_launcher, ("Metro Arrival Alert"),
				        System.currentTimeMillis());
				Intent notificationIntent = new Intent(ArrivalNotifyService.this, ArrivalNotifyService.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(ArrivalNotifyService.this, 0, notificationIntent, 0);
				
				notification.setLatestEventInfo(ArrivalNotifyService.this, msg1,
				        msg2, pendingIntent);
				startForeground(294, notification);
				
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
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) {
					System.out.println("Start document");
				} else if(eventType == XmlPullParser.END_DOCUMENT) {
					System.out.println("End document");
				} else if(eventType == XmlPullParser.START_TAG) {
					String name = xpp.getName();
					System.out.println("Start tag "+name);
					if(name.equals( "prediction" )) {

						String timeString = xpp.getAttributeValue(null, "minutes");

						int predTime = Integer.valueOf(timeString); 
						if (predTime >= 0 && ( predTime < time || time < 0) )
						{
							time = predTime;
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
