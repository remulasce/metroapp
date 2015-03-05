/*
 package com.remulasce.lametroapp.components.persistence;

import android.content.Context;
import android.util.Log;

import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

**
 * Created by Remulasce on 1/11/2015.
 *
 * It just uses the default object serialization to save to files.
 * So, it relies on pretty much the entire stack being Serializable, except for some
 * objects which manually serialize themselves to avoid spreading too far.
 *
public class SerializedFileFieldSaver implements FieldSaver {
    private static final String SERVICE_REQUESTS_SER = "serviceRequests.ser";
    private static final String TAG = "SettingFieldSaver";



    private final Context context;

    public SerializedFileFieldSaver(Context c, StopLocationTranslator locations) {
        context = c;
    }

    @Override
    public void saveServiceRequests(Collection<ServiceRequest> requests) {
        Log.d(TAG, "Saving service requests: "+requests.size()+ " "+requests);

        FileOutputStream fos;
        try {
            fos = context.openFileOutput(SERVICE_REQUESTS_SER, Context.MODE_PRIVATE);

            ObjectOutputStream oos;
            oos = new ObjectOutputStream(fos);

            oos.writeObject(requests);

            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Saved "+requests.size()+" service requests");
    }

    @Override
    public Collection<ServiceRequest> loadServiceRequests() {
        Log.d(TAG, "Loading service requests");

        Collection<ServiceRequest> emptyRequests = new ArrayList<ServiceRequest>();
        try
        {
            FileInputStream fileIn = context.openFileInput(SERVICE_REQUESTS_SER);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            Object o = in.readObject();
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

            Log.d(TAG, "Loaded up "+ret.size()+" requests");

            return ret;
        }
        catch(IOException e)
        {
            Log.i(TAG, "No persistent requests found");
            return emptyRequests;
        }catch(ClassNotFoundException c)
        {
            c.printStackTrace();
            return emptyRequests;
        }
    }
}
*/