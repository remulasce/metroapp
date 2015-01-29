package com.remulasce.lametroapp.components.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.remulasce.lametroapp.basic_types.ServiceRequest;
import com.remulasce.lametroapp.basic_types.StopServiceRequest;
import com.remulasce.lametroapp.dynamic_data.types.Prediction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Remulasce on 1/11/2015.
 */
public class SettingFieldSaver implements FieldSaver {
    private static final String PREFERENCES_NAME = "Fields";

    private static final String SERVICEREQUEST_COUNT_NAME = "servicerequest_count";
    private static final String SERVICEREQUEST_ITEM_NAME = "servicerequest_";


    private SharedPreferences preferences;

    public SettingFieldSaver(Context c) {
        preferences = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
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
        SharedPreferences.Editor editor = preferences.edit();

        int requestCount = requests.size();
        editor.putInt(SERVICEREQUEST_COUNT_NAME, requestCount);

        int i = 0;
        for (ServiceRequest request : requests) {
//            Set<String> put = new HashSet<String>();
            String requestNameTerminated = SERVICEREQUEST_ITEM_NAME + i +"_";

            Collection<String> stopids = request.getRaw();
            editor.putInt(requestNameTerminated+"numstops", request.getRaw().size());

            int stopNum = 0;
            for (String s : stopids) {
//                put.add("0 " + s);
                editor.putString(requestNameTerminated+request.getRaw() + "stop"+stopNum, s);

                stopNum++;
            }
//            put.add("1 " + request.getDisplayName());
            editor.putString(requestNameTerminated+"displayname", request.getDisplayName());
//            put.add("2 " + request.isInScope());
            editor.putString(requestNameTerminated+"inscope", String.valueOf(request.isInScope()));


//            Only supported in API 11
//            editor.putStringSet(SERVICEREQUEST_ITEM_NAME + i, put);
            i++;
        }

        editor.commit();
        Log.d("SettingFieldSaver", "Saved "+i+" service requests");
    }

    @Override
    public Collection<ServiceRequest> loadServiceRequests() {
        Collection<ServiceRequest> ret = new ArrayList<ServiceRequest>();

        int requestCount = preferences.getInt(SERVICEREQUEST_COUNT_NAME, 0);

        for (int i = 0; i < requestCount; i++) {
//            Set<String> set = new HashSet<String>();
//            set = preferences.getStringSet(SERVICEREQUEST_ITEM_NAME + i, set);
//
//            Collection<String> raw = new ArrayList<String>();
//            String displayname = "bad request load";
//            String scope = "bad request load";
//
//            for (String s : set) {
//                if (s.startsWith("0")) {
//                    raw.add(s.substring(2));
//                }
//                if (s.startsWith("1")) {
//                    displayname = s.substring(2);
//                }
//                if (s.startsWith("2")) {
//                     scope = s.substring(2);
//                }

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
                stopIDs.add(preferences.getString(requestName+"stop"+ii, null));
            }


            ServiceRequest add = new StopServiceRequest(stopIDs, displayname);
            if (scope.equals("false")) { add.descope(); }

            if (!add.isValid()) {
                Log.w("SettingFieldSaver", "Couldn't load a ServiceRequest");
            } else {
                ret.add(add);
            }
        }
        Log.d("SettingFieldSaver", "Loaded up to "+requestCount+" requests");
        return ret;
    }
}
