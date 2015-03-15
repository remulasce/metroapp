package com.remulasce.lametroapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.remulasce.lametroapp.analytics.Tracking;
import com.remulasce.lametroapp.basic_types.Destination;
import com.remulasce.lametroapp.basic_types.Route;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.Vehicle;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public class ArrivalNotifyService extends Service {

    private NetTask netTask;
	private NotificationTask notificationTask;

	private class NetTask implements Runnable {

	    boolean run = true;

	    public int stopID;
        public String stopName;
	    public String vehicleNumber;
	    public String agency;
	    public String routeName;
	    public String destination;
        public int notificationTime = 90;

	    public boolean isValid = false;
	    
	    int runNum = 0;

	    public long arrivalTime = 0;
	    public long arrivalUpdatedAt = 0;
	    
	    public String lastDestination = "";
	    
		@Override
		public void run() {
			StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			// Prevents confusion in the notification handler
			arrivalUpdatedAt = System.currentTimeMillis();
			
			while (run) {

				String response = getXMLArrivalString(stopID, agency, routeName);				
				StupidArrival arrival = getFirstArrivalTime(response, destination, vehicleNumber);
				int seconds = arrival.arrivalTime;

				if (seconds != -1) {
				    isValid = true;
				    lastDestination = arrival.destination;
                    stopName = arrival.stopName;
					arrivalTime = System.currentTimeMillis() + seconds * 1000;
					arrivalUpdatedAt = System.currentTimeMillis();
	 
					if (runNum == 0) {
                        toast ("Next arrival "+LaMetroUtil.timeToDisplay(seconds));
					}
				}
				
				runNum++;
				if (runNum >= 100) run = false;
				
				
				try {
					Thread.sleep(Math.min(5000 + seconds * 200, 240 * 1000));
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
		  //Helper fxn, since we only make Types long enough to check validity,
	    // we should clear the underlying invalid Strings
          void cleanParameters() {
	        Stop s = new Stop(stopID);
	        Route r = new Route(routeName);
	        Destination d = new Destination(destination);
	        Vehicle v = new Vehicle(vehicleNumber);
	        
	        if (!s.isValid()) stopID = 0;
	        if (!r.isValid()) routeName = null;
	        if (!d.isValid()) destination = null;
	        if (!v.isValid()) vehicleNumber = null;
	    }
		
	    boolean parametersValid() {
	        try {
	            // We only check Stop, because that's the minimum
	            // we need.
	            
	            Stop s = new Stop(stopID);
	            if (!s.isValid()) return false;
	            
	            if (agency == null || agency.isEmpty()) return false;
	        } catch (Exception e) {
	            return false;
	        }
	        
	        return true;
	    }
	}

	private class NotificationTask implements Runnable {
		
	    public boolean run = true;

	    public int lastDisplayedSeconds = 10000;
	    
	    public NotificationTask() {
	    }
	    
		@Override
		public void run() {

			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(ArrivalNotifyService.this);
            Intent cancelIntent = new Intent();
            cancelIntent.setAction("com.remulasce.lametroapp.cancel_notification");

            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(ArrivalNotifyService.this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_action_remove, "Cancel", cancelPendingIntent);


            while (run) {
				updateNotificationText(mBuilder);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}


        public void updateNotificationText( final NotificationCompat.Builder mBuilder ) {
	        Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());     
	        
	        String msg1;
	        String msg2;
	        
	        final int secondsTillArrival = (int)(netTask.arrivalTime - System.currentTimeMillis()) / 1000;
	        final int minutesSinceEstimate = (int)(System.currentTimeMillis() - netTask.arrivalUpdatedAt) / 1000 / 60; 
	        
	        final String destination = netTask.destination;
	        final String lastDestination = netTask.lastDestination;
	        final String vehicleNumber = netTask.vehicleNumber;
	        final String routeName = netTask.routeName;
            final String stopName = netTask.stopName;
	        final int stopID = netTask.stopID;
            final int notificationTime = netTask.notificationTime;
	        
	        boolean vibrate = false;
	        
    		if ( netTask.runNum > 5 ) {
    		    if ( secondsTillArrival < -30 ) {
    	        	Log.e("NotifyService", "NotifyService ending because the vehicle has arrived");

                    Tracking.sendEvent("NotifyService", "Service Ending", "Vehicle arrived");
        	        ShutdownService();
                    return;
    		    }
    		    if ( minutesSinceEstimate > 5 ) {
                	Log.e("NotifyService", "NotifyService ending because we haven't received an estimate in a while");

                    Tracking.sendEvent("NotifyService", "Service Ending", "Estimate timed out");
                	ShutdownService();
                    return;
    		    }
	        }
	        
	        if ( !netTask.isValid || minutesSinceEstimate < 0 || minutesSinceEstimate > 5) {
	            msg2 = "Getting prediction...";
	            if (destination != null) {
                    msg2 += "\n";
	                msg2 += "\n" + destination;
	            }
	        }
	        else if (secondsTillArrival <= 0) {
	            msg2 = "Vehicle arrived";
                msg2 += "\n" + stopName;
	            msg2 += "\n" + lastDestination;
	        }
	        else if (secondsTillArrival <= 90) {
	            msg2 = secondsTillArrival+" seconds";
                msg2 += "\n" + stopName;
	            msg2 += "\n" + lastDestination;

                if( lastDisplayedSeconds > notificationTime && secondsTillArrival < notificationTime) {
                    vibrate = true;
                }

	            lastDisplayedSeconds = secondsTillArrival;
	        }
	        else {
	            msg2 = (secondsTillArrival/60)+" minutes";
                msg2 += "\n" + stopName;
	            msg2 += "\n" + lastDestination;

                if( lastDisplayedSeconds > notificationTime && secondsTillArrival < notificationTime) {
                    vibrate = true;
                }

	            lastDisplayedSeconds = secondsTillArrival;
	        }
	            
	        if (minutesSinceEstimate >= 5) { msg2 += " : "+minutesSinceEstimate; }
	        
	        
	        if (vehicleNumber != null) {
	            msg1 = "Waiting for veh #" + vehicleNumber;
	        }
	        else if (routeName != null && !routeName.isEmpty()) {
	            msg1 = "Waiting for line "+routeName;
	        }
	        else {
	            msg1 = "Waiting at stop "+stopID;
	        }
	        
	        final String dispTitle = msg1;
	        final String dispText = msg2;
	        final boolean doVibrate = vibrate;
	        
	        h.post(new Runnable() {
	            @Override
	            public void run() {
	                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
	                bigTextStyle.setBigContentTitle(dispTitle);
	                bigTextStyle.bigText(dispText);

	                
	                mBuilder
                            .setSmallIcon(R.mipmap.ic_launcher)
	                        .setContentTitle(dispTitle)
	                        .setContentText(dispText)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
	                        .setStyle(bigTextStyle);
	                        
	                if ( doVibrate ) {
                        Uri uri = Uri.parse("android.resource://"
                                + ArrivalNotifyService.this.getPackageName() + "/" + R.raw.notification_custom);
                        mBuilder.setSound(uri, AudioManager.STREAM_ALARM);

                        toast("Vehicle arrives " + LaMetroUtil.timeToDisplay(secondsTillArrival));
	                    
	                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	                    v.vibrate(2000);
	                } else {
	                    mBuilder.setSound(null);
	                }


                    Intent resultIntent = new Intent(ArrivalNotifyService.this, MainActivity.class);
	                resultIntent.putExtra("Route", routeName);
	                resultIntent.putExtra("StopID", String.valueOf(stopID));
	                resultIntent.putExtra("VehicleNumber", vehicleNumber);

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
	            }
	        });
	    }
	}
	

	

	
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // Meh good measure
	    if (netTask != null || notificationTask != null) {
	       ShutdownService();
	    }
	    
        netTask = new NetTask();
        notificationTask = new NotificationTask();
        
		netTask.agency			= intent.getExtras().getString("Agency");
		netTask.stopID			= intent.getExtras().getInt("StopID");
		netTask.routeName		= intent.getExtras().getString("Route");
		netTask.destination		= intent.getExtras().getString("Destination");
		netTask.vehicleNumber	= intent.getExtras().getString("VehicleNumber");
        netTask.notificationTime= intent.getExtras().getInt("NotificationTime", 120);
		
		netTask.cleanParameters();
		
		if (!netTask.parametersValid()) {
		    Log.e("NotifyService", "Bad input into ArrivalNotify Service");
            Tracking.sendEvent("NotifyService", "Bad input in notify service start");

		    return Service.START_NOT_STICKY;
		}
		
		netTask.run = true;
		notificationTask.run = true;

		Thread netThread = new Thread(netTask, "NotifyNetTask");
		netThread.start();
		
		Thread notificationThread = new Thread(notificationTask, "NotifyDisplayTask");
		notificationThread.start();

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	void ShutdownService() {
	    Log.i("NotifyService", "Shutting down service");
	    
	    if ( netTask != null ) { 
	        netTask.run = false;
	        netTask = null;
	    }
	    if ( notificationTask != null ) {
	        notificationTask.run = false;
	        notificationTask = null;
	    }
	    
	    new Handler(ArrivalNotifyService.this.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
            }
        });
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		ShutdownService();
	}

	void toast(final String msg) {
		Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());

		h.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ArrivalNotifyService.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	

	private class StupidArrival {
	    public String destination;
	    public int arrivalTime;
        public String stopName;
    }
	StupidArrival getFirstArrivalTime(String xml, String destination, String vehicleNumber) {
		int time = -1;
		String lastDestination = "";

        String stopName = "";

		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader (xml));
			int eventType = xpp.getEventType();
			String curDirection = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String name = xpp.getName();

                    if(name.equals("predictions")) { stopName = xpp.getAttributeValue(null, "stopTitle"); }
					if(name.equals("direction")) { curDirection = xpp.getAttributeValue(null, "title"); }
					if(name.equals( "prediction" )) {

						String timeString = xpp.getAttributeValue(null, "seconds");
						String vehicleNum = xpp.getAttributeValue(null, "vehicle");

						int predTime = Integer.valueOf(timeString); 
						if (predTime >= 0 && ( predTime < time || time < 0) )
						{
							if ( ! (destination == null || destination.equals(curDirection))) {	}
							else if ( vehicleNumber != null && !vehicleNumber.equals(vehicleNum) ) {
								
							}
							else {
								time = predTime;
								lastDestination = curDirection;
							}
						}
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StupidArrival ret = new StupidArrival();
		ret.arrivalTime = time;
        ret.stopName = stopName;
		ret.destination = lastDestination;
		return ret;
	}
	
	String getXMLArrivalString(int stopID, String agency, String routeName) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String URI = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a="+agency+"&stopId="+stopID;
		if (routeName != null && !routeName.isEmpty()) {
			URI += "&routeTag="+routeName;
		}
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
