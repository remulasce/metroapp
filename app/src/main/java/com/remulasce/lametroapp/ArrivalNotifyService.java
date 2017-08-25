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
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.remulasce.lametroapp.java_core.LaMetroUtil;
import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.Agency;
import com.remulasce.lametroapp.java_core.basic_types.Destination;
import com.remulasce.lametroapp.java_core.basic_types.Route;
import com.remulasce.lametroapp.java_core.basic_types.Stop;
import com.remulasce.lametroapp.java_core.basic_types.Vehicle;
import com.remulasce.lametroapp.java_core.dynamic_data.types.Arrival;
import com.remulasce.lametroapp.java_core.static_data.StopNameTranslator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ArrivalNotifyService extends Service {

    public static final String TAG = "NotifyService";
    private NetTask netTask;
	private NotificationTask notificationTask;

    // Unit testing purposes
    public static boolean test_started = false;
    public static boolean test_created = false;

	private class NetTask implements Runnable {

	    boolean run = true;

	    public String stopID;
        public String stopName;
	    public String vehicleNumber;
	    public String agency;
	    public String routeName;
	    public String destinationFromIntent;
        public int notificationTime = 90;

	    public boolean hasPrediction = false;
	    
	    int runNum = 0;

	    public long arrivalTime = 0;
	    public long arrivalUpdatedAt = 0;
	    
	    public String lastDestination = "";
	    
		@Override
		public void run() {
			// Prevents confusion in the notification handler
			arrivalUpdatedAt = System.currentTimeMillis();
			
			while (run) {

				Log.d(TAG, "Notify service updating from network");

				String response = getXMLArrivalString(stopID, agency, routeName);				
				StupidArrival arrival = getFirstArrivalTime(response,  destinationFromIntent, vehicleNumber, agency);
				
				int seconds = -1;
				if (arrival != null) {
					seconds = arrival.arrivalTime;
				}

				if (seconds != -1) {
				    hasPrediction = true;
				    lastDestination = arrival.destination;
                    stopName = arrival.stopName;
					arrivalTime = System.currentTimeMillis() + seconds * 1000;
					arrivalUpdatedAt = System.currentTimeMillis();
	 
					if (runNum == 0) {
                        toast ("Next arrival "+ LaMetroUtil.timeToDisplay(seconds));
					}
				} else {
					Log.w(TAG, "Couldn't get prediction from server");
				}
				
				runNum++;
				if (runNum >= 100) run = false;
				
				
				try {
					if (seconds != -1) {
						Thread.sleep(Math.min(5000 + seconds * 200, 60 * 1000));
//					Thread.sleep(Math.min(5000 + seconds * 200, 240 * 1000));
					} else {
						Thread.sleep(10000);
					}
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
		  //Helper fxn, since we only make Types long enough to check validity,
	    // we should clear the underlying invalid Strings
          void cleanParameters() {
	        Stop s = new Stop(stopID);
	        Route r = new Route(routeName);
	        Destination d = new Destination(destinationFromIntent);
	        Vehicle v = new Vehicle(vehicleNumber);
	        
	        if (!s.isValid()) stopID = null;
	        if (!r.isValid()) routeName = null;
	        if (!d.isValid()) destinationFromIntent = null;
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

        public int lastDisplayedEstimateSeconds = 10000;

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
                Log.d(TAG, "Notification update thread loop...");
                updateNotificationText(mBuilder);
                try {
                    if (netTask != null && netTask.hasPrediction) {
                        Thread.sleep(5000);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /** Appends the stop name if we know it */
        private String maybeAppendStopName(String msg) {
			String stop = getStopText();

			if (stop != null) {
				return msg + "\n" + netTask.stopName;
			}

			return msg;
        }

        /** Appends the destination name if we know it, from either network or intent. */
        private String maybeAppendDestination(String msg) {
			String destination = getDestinationText();

			if (destination != null) {
            	return msg + "\n" + destination;
			}

			return msg;
        }

        private String maybeAddStopAndDestinationBody(String msg) {
            msg = maybeAppendStopName(msg);
            msg = maybeAppendDestination(msg);

            return msg;
        }

        private String getStopText() {
			if (netTask.stopName != null && !netTask.stopName.isEmpty()) {
				return netTask.stopName;
			}

			return null;
		}

		/**
		 * Gets the destination, either from the most recent network return or from the original
		 * intent, or null
		 */
		private String getDestinationText() {
			if (netTask.lastDestination != null && !netTask.lastDestination.isEmpty()) {
				return netTask.lastDestination;
			} else if (netTask.destinationFromIntent != null && !netTask.destinationFromIntent.isEmpty()) {
				return netTask.destinationFromIntent;
			}

			return null;
		}

		/**
		 * Makes the text body of the notification
		 */
        private String makeMessageBody(
        		boolean hasPrediction,
				int secondsTillArrival,
				int secondsSinceEstimate) {
			String msg2;

			if ( !hasPrediction || secondsSinceEstimate < 0 ) {
				msg2 = "Getting prediction...";
				msg2 = maybeAddStopAndDestinationBody(msg2);
			} else {
				msg2 = makeVehiclePredictionText(secondsTillArrival);
				msg2 = maybeAddStopAndDestinationBody(msg2);

				if (secondsSinceEstimate >= 0) {
					msg2 += "\nupdated " + secondsSinceEstimate + " seconds ago";
				}
			}

			return msg2;
		}

		/**
		 * Makes either the "x seconds" or "arrived" text line
		 */
		private String makeVehiclePredictionText(int secondsTillArrival) {
			String msg2;
			if (secondsTillArrival <= 0) {
                msg2 = "Vehicle arrived";
            } else if (secondsTillArrival <= 90) {
                msg2 = secondsTillArrival + " seconds";
            } else {
                msg2 = (secondsTillArrival / 60) + " minutes";
            }
			return msg2;
		}

		public void updateNotificationText( final NotificationCompat.Builder mBuilder ) {
	        if (netTask == null) {
				Log.w(TAG, "Notification thread waiting on null netTask");
				return;
			}

			Handler h = new Handler(ArrivalNotifyService.this.getMainLooper());
	        
	        String msg1;
	        String msg2;
	        
	        final int secondsTillArrival = (int)(netTask.arrivalTime - System.currentTimeMillis()) / 1000;
	        final int secondsSinceEstimate = (int)(System.currentTimeMillis() - netTask.arrivalUpdatedAt) / 1000;

	        final String vehicleNumber = netTask.vehicleNumber;
	        final String routeName = netTask.routeName;
	        final String stopID = netTask.stopID;
            final int notificationTime = netTask.notificationTime;
	        
	        boolean vibrate = false;
	        
    		if ( netTask.runNum > 5 ) {
    		    if ( secondsTillArrival < -30 ) {
    	        	Log.e(TAG, "NotifyService ending because the vehicle has arrived");

                    Tracking.sendEvent(TAG, "Service Ending", "Vehicle arrived");
        	        ShutdownService();
                    return;
    		    }
				/*
				"No Service" is an accepted use case
				eg. Going from blue -> red line underground
    		    if ( minutesSinceEstimate > 5 ) {
                	Log.e(TAG, "NotifyService ending because we haven't received an estimate in a while");

                    Tracking.sendEvent(TAG, "Service Ending", "Estimate timed out");
                	ShutdownService();
                    return;
    		    }
    		    */
	        }
	        
			msg2 = makeMessageBody(secondsTillArrival, secondsSinceEstimate);

			// Check whether to show the notification / vibration
			if (netTask.hasPrediction && secondsSinceEstimate > 0) {
				// Vibrate when notification time is hit
				if (lastDisplayedEstimateSeconds > notificationTime && secondsTillArrival < notificationTime) {
					vibrate = true;
				}

				// Pass forward last displayed seconds
				lastDisplayedEstimateSeconds = secondsTillArrival;
			}

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
			final boolean useImportantIcon = secondsTillArrival < notificationTime;
	        
	        h.post(new Runnable() {
	            @Override
	            public void run() {
	                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
	                bigTextStyle.setBigContentTitle(dispTitle);
	                bigTextStyle.bigText(dispText);

	                
	                mBuilder
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

					if (useImportantIcon) {
						mBuilder.setSmallIcon(R.mipmap.important_icon_3);
					} else {
						mBuilder.setSmallIcon(R.mipmap.ic_launcher);
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
        test_started = true;

	    // Meh good measure
	    if (netTask != null || notificationTask != null) {
	       ShutdownService();
	    }
	    
        netTask = new NetTask();
        notificationTask = new NotificationTask();
        
		netTask.agency					= intent.getExtras().getString("Agency");
		netTask.stopID					= intent.getExtras().getString("StopID");
		netTask.routeName				= intent.getExtras().getString("Route");
		netTask.destinationFromIntent 	= intent.getExtras().getString("Destination");
		netTask.vehicleNumber			= intent.getExtras().getString("VehicleNumber");
        netTask.notificationTime		= intent.getExtras().getInt("NotificationTime", 120);
		
		netTask.cleanParameters();
		
		if (!netTask.parametersValid()) {
		    Log.e(TAG, "Bad input into ArrivalNotify Service");
            Tracking.sendEvent(TAG, "Bad input in notify service start");

			Toast.makeText(this, "Notify Service couldn't track this request",Toast.LENGTH_SHORT).show();

		    return Service.START_NOT_STICKY;
		}

        test_started = true;
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

    @Override
    public void onCreate() {
        Log.i(TAG, "Notify service onCreate");
        test_created = true;
    }

	void ShutdownService() {
	    Log.i(TAG, "Shutting down service");
	    
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
	StupidArrival getFirstArrivalTime(String xml, String destination, String vehicleNumber, String agency) {

		List<Arrival> parsedArrivals = LaMetroUtil.parseAllArrivals(xml, new Agency(agency, null, null, null));

		if (parsedArrivals == null) {
			return null;
		}

		int time = -1;
		String lastDestination = "";
        String stopName = "";


		for (Arrival a : parsedArrivals) {
			if (!a.getDirection().getString().equals(destination)) {
				continue;
			}
			if (vehicleNumber != null && !vehicleNumber.isEmpty() && !vehicleNumber.equals(a.getVehicleNum().getString())) {
				continue;
			}

			stopName = a.getStop().getStopName();
			lastDestination = a.getDirection().getString();

			if (time == -1 || a.getEstimatedArrivalSeconds() < time) {
				time = (int) a.getEstimatedArrivalSeconds();
			}
		}

		
		StupidArrival ret = new StupidArrival();
		ret.arrivalTime = time;
        ret.stopName = stopName;
		ret.destination = lastDestination;

		return ret;
	}
	
	String getXMLArrivalString(String stopID, String agency, String routeName) {

		Stop tempStop = new Stop(stopID);
		Agency tempAgency = new Agency(agency, agency, null, null);
		Route tempRoute = new Route(routeName);

		tempStop.setAgency(tempAgency);

		String URI = LaMetroUtil.makePredictionsRequest(tempStop, tempRoute);

		String ret = getStringFromNet(URI);
		return ret;
	}

	@NonNull
	private String getStringFromNet(String URI) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
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
