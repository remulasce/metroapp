package com.remulasce.lametroapp.components.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.remulasce.lametroapp.java_core.analytics.Tracking;
import com.remulasce.lametroapp.java_core.basic_types.ServiceRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/11/2015.
 *
 * <p>It just uses the default object serialization to save to files. So, it relies on pretty much
 * the entire stack being Serializable, except for some objects which manually serialize themselves
 * to avoid spreading too far.
 */
public class SerializedFileFieldSaver implements FieldSaver {
  private static final String SERVICE_REQUESTS_SER = "serviceRequests.ser";
  private static final String TAG = "FileFieldSaver";

  private final Context context;

  public SerializedFileFieldSaver(Context c) {
    context = c;
  }

  @Override
  public void saveServiceRequests(Collection<ServiceRequest> requests) {
    Log.d(TAG, "Saving service requests: " + requests.size() + " " + requests);

    FileOutputStream fos;
    try {
      fos = context.openFileOutput(SERVICE_REQUESTS_SER, Context.MODE_PRIVATE);

      ObjectOutputStream oos;
      oos = new ObjectOutputStream(fos);

      oos.writeObject(requests);
      oos.writeLong(System.currentTimeMillis());

      oos.close();
    } catch (IOException e) {
      Tracking.sendEvent("Errors", "SerializedFileFieldSaver", "Exception in saveServiceRequests");
      e.printStackTrace();
    }

    Log.d(TAG, "Saved " + requests.size() + " service requests");
  }

  @Override
  public Collection<ServiceRequest> loadServiceRequests(long stalenessMillis) {
    Log.d(TAG, "Loading service requests");

    Collection<ServiceRequest> emptyRequests = new ArrayList<ServiceRequest>();
    try {
      FileInputStream fileIn = context.openFileInput(SERVICE_REQUESTS_SER);
      ObjectInputStream in = new ObjectInputStream(fileIn);

      Object o = in.readObject();
      try {
        long saveTime = in.readLong();

        Log.i(
            TAG, "Persisted stops last saved " + (System.currentTimeMillis() - saveTime) + " ago");
        // Don't return stale trips
        if (stalenessMillis != -1 && System.currentTimeMillis() >= saveTime + stalenessMillis) {
          Log.i(TAG, "Saved requests are stale, skipping");
          return null;
        }
      } catch (Exception e) {
        Log.i(TAG, "No timestamp found for saved requests, assuming fresh");
      }

      Collection<ServiceRequest> ret;

      try {
        ret = (Collection<ServiceRequest>) o;
      } catch (ClassCastException e) {
        e.printStackTrace();
        Log.e(TAG, "Wrong type loaded from file, returning new empty list");
        ret = new ArrayList<ServiceRequest>();
      }

      in.close();
      fileIn.close();

      Log.d(TAG, "Loaded up " + ret.size() + " requests");

      return ret;
    } catch (IOException e) {
      // This is a normal case- user has no saved requests.
      // Not an error.
      //            Tracking.sendEvent("Errors", "SerializedFileFieldSaver", "Exception in
      // loadServiceRequests");
      Log.i(TAG, "No persistent requests found");
      return emptyRequests;
    } catch (ClassNotFoundException c) {
      Tracking.sendEvent(
          "Errors", "SerializedFileFieldSaver", "Class Not Found Exception in loadServiceRequests");
      c.printStackTrace();
      return emptyRequests;
    } catch (ClassCastException c) {
      Tracking.sendEvent(
          "Errors", "SerializedFileFieldSaver", "ClassCast Exception in loadServiceRequests");
      c.printStackTrace();
      return emptyRequests;
    } catch (Exception e) {
      Tracking.sendEvent(
          "Errors", "SerializedFileFieldSaver", "General Exception in loadServiceRequests");
      e.printStackTrace();
      return emptyRequests;
    }
  }

  @Override
  public void saveObject(String key, Object object) {
    Log.d(TAG, "Saving " + key + " object: " + object);

    FileOutputStream fos;
    try {
      fos = context.openFileOutput(getFilename(key), Context.MODE_PRIVATE);

      ObjectOutputStream oos;
      oos = new ObjectOutputStream(fos);

      oos.writeObject(object);

      oos.close();
    } catch (IOException e) {
      Tracking.sendEvent("Errors", "SerializedFileFieldSaver", "Exception in saveObject");
      e.printStackTrace();
    }
  }

  @NonNull
  private String getFilename(String key) {
    return key + ".ser";
  }

  @Override
  public Object loadObject(String key) {
    Log.d(TAG, "Loading service requests");

    Collection<ServiceRequest> emptyRequests = new ArrayList<ServiceRequest>();
    try {
      FileInputStream fileIn = context.openFileInput(getFilename(key));
      ObjectInputStream in = new ObjectInputStream(fileIn);

      Object o = in.readObject();
      return o;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (OptionalDataException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (StreamCorruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Log.i(TAG, "No object found for key " + key);

    return null;
  }
}
