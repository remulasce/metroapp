package com.remulasce.lametroapp.components.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.basic_types.Stop;
import com.remulasce.lametroapp.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.static_data.StopLocationTranslator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 1/11/2015.
 */
public class SerializedFileFieldSaver implements FieldSaver {
    public static final String SERVICE_REQUESTS_SER = "serviceRequests.ser";
    public static final String SETTING_FIELD_SAVER = "SettingFieldSaver";


    private StopLocationTranslator locations;

    Context context;

    public SerializedFileFieldSaver(Context c, StopLocationTranslator locations) {
        this.locations = locations;
        //TODO
        context = c;
    }

    /* Format:
        OLD:
        We internally number how many requests we have in servicerequest_count int
        then in servicerequest_xx, we put a string set (unordered)
        0 request.raw1
        0 request.raw2
        1 request.displayname,
        2 request.inscope

        We add as many raw#s as we have tracked.


        NEW: (to work around putStringSet):
        Instead of a set in servicerequest_xx, we put:
        servicerequest_xx_numstops: int number of stopids
        servicerequest_xx_stopx: Each stopID string
        servicerequest_xx_displayname: displayname
        servicerequest_xx_inscope: Scope

     */
    @Override
    public void saveServiceRequests(Collection<ServiceRequest> requests) {

//        int requestCount = requests.size();

//        editor.putInt(SERVICEREQUEST_COUNT_NAME, requestCount);

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(SERVICE_REQUESTS_SER, Context.MODE_PRIVATE);

            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(fos);

            oos.writeObject(requests);

            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(SETTING_FIELD_SAVER, "Saved "+requests.size()+" service requests");

        return;

        /*
        int i = 0;
        for (ServiceRequest request : requests) {

            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput("serviceRequest"+i+".ser", Context.MODE_PRIVATE);

                ObjectOutputStream oos = null;
                oos = new ObjectOutputStream(fos);

                oos.writeObject(request);

                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }





            String requestNameTerminated = SERVICEREQUEST_ITEM_NAME + i +"_";

            Collection<String> stopids = request.getRaw();
            editor.putInt(requestNameTerminated + "numstops", request.getRaw().size());

            int stopNum = 0;
            for (String s : stopids) {
                editor.putString(requestNameTerminated+"stop"+stopNum, s);

                stopNum++;
            }
            editor.putString(requestNameTerminated+"displayname", request.getDisplayName());
            editor.putString(requestNameTerminated+"inscope", String.valueOf(request.isInScope()));

            i++;
        }

        editor.commit();


        */
    }

    @Override
    public Collection<ServiceRequest> loadServiceRequests() {
        Collection<ServiceRequest> emptyRequests = new ArrayList<ServiceRequest>();
        try
        {
            FileInputStream fileIn = context.openFileInput(SERVICE_REQUESTS_SER);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Collection<ServiceRequest> ret = (Collection<ServiceRequest>) in.readObject();

            in.close();
            fileIn.close();

            Log.d(SETTING_FIELD_SAVER, "Loaded up "+ret.size()+" requests");

            return ret;
        }
        catch(IOException e)
        {
            Log.i(SETTING_FIELD_SAVER, "No persistent requests found");
            return emptyRequests;
        }catch(ClassNotFoundException c)
        {
            c.printStackTrace();
            return emptyRequests;
        }
/*

        int requestCount = preferences.getInt(SERVICEREQUEST_COUNT_NAME, 0);

        for (int i = 0; i < requestCount; i++) {
//            NEW: (to work around putStringSet):
//            Instead of a set in servicerequest_xx, we put:
//            servicerequest_xx_numstops: int number of stopids
//            servicerequest_xx_stopx: Each stopID string
//            servicerequest_xx_displayname: displayname
//            servicerequest_xx_inscope: Scope


            int numstops;
            Collection<String> stopIDs = new ArrayList<String>();
            String displayname;
            String scope;


            String requestName = "servicerequest_"+i+"_";

            displayname = preferences.getString(requestName+"displayname", null);
            scope = preferences.getString(requestName+"inscope", null);

            numstops = preferences.getInt(requestName + "numstops", 0);
            for (int ii = 0; ii < numstops; ii++) {
                String id = preferences.getString(requestName+"stop"+ii, null);
                if (id != null) {
                    stopIDs.add(id);
                }
            }

            Collection<Stop> stops = new ArrayList<Stop>();
            for (String id : stopIDs) {
                Stop stop = new Stop(id);
                stop.setLocation(locations.getStopLocation(stop));
                stops.add(stop);
            }

            ServiceRequest add = new StopServiceRequest(stops, displayname);
            if (scope.equals("false")) { add.descope(); }

            if (!add.isValid()) {
                Log.w("SettingFieldSaver", "Couldn't load a ServiceRequest");
            } else {
                ret.add(add);
            }
        }
        Log.d("SettingFieldSaver", "Loaded up to "+requestCount+" requests");
        return ret;
        */

    }
}
