package com.remulasce.lametroapp.components.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.remulasce.lametroapp.basic_types.ServiceRequest;

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
        We internally number how many requests we have in servicerequest_count int
        then in servicerequest_xx, we put a string set (unordered)
        0 request.raw,
        1 request.displayname,
        2 request.inscope

        The numbers are a natural ordering hack.
     */
    @Override
    public void saveServiceRequests(Collection<ServiceRequest> requests) {
        SharedPreferences.Editor editor = preferences.edit();

        int requestCount = requests.size();
        editor.putInt(SERVICEREQUEST_COUNT_NAME, requestCount);

        int i = 0;
        for (ServiceRequest request : requests) {
            Set<String> put = new HashSet<String>();
            put.add("0 "+request.getRaw());
            put.add("1 " + request.getDisplayName());
            put.add("2 "+request.isInScope());

            editor.putStringSet(SERVICEREQUEST_ITEM_NAME + i, put);
            i++;
        }

        editor.commit();
    }

    @Override
    public Collection<ServiceRequest> loadServiceRequests() {
        Collection<ServiceRequest> ret = new ArrayList<ServiceRequest>();

        int requestCount = preferences.getInt(SERVICEREQUEST_COUNT_NAME, 0);

        for (int i = 0; i < requestCount; i++) {
            Set<String> set = new HashSet<String>();
            set = preferences.getStringSet(SERVICEREQUEST_ITEM_NAME + i, set);

            String raw = "bad request load";
            String displayname = "bad request load";
            String scope = "bad request load";

            for (String s : set) {
                if (s.startsWith("0")) {
                    raw = s.substring(2);
                }
                if (s.startsWith("1")) {
                    displayname = s.substring(2);
                }
                if (s.startsWith("2")) {
                     scope = s.substring(2);
                }
            }

            ServiceRequest add = new ServiceRequest(raw);
            add.setDisplayName(displayname);
            if (scope.equals("false")) { add.descope(); }

            if (!add.isValid()) {
                Log.w("SettingFieldSaver", "Couldn't load a ServiceRequest");
            } else {
                ret.add(add);
            }
        }

        return ret;
    }
}
