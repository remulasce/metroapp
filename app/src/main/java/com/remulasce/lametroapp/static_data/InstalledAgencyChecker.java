package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Remulasce on 8/23/2015.
 *
 * This thing should be consulted at startup to figure out what agency .db files are actually present on-device.
 * That's, ah. Basically it.
 *
 * It'll look in the assets folder for every .db file, and pull the overview info out.
 */
public class InstalledAgencyChecker {


    public static final String DATABASES = "databases";
    public static final String TAG = "InstalledAgencies";
    Context c;

    public InstalledAgencyChecker(Context c) {
        this.c = c;
    }

    public Collection<Agency> getInstalledAgencies() {
        Collection<Agency> ret = new ArrayList<Agency>();

        Resources res = c.getResources();
        AssetManager am = res.getAssets();

        try {
            String[] fileList = am.list(DATABASES);

            for (int i = 0; i < fileList.length; i++) {
                Log.d("", fileList[i]);

                // We need to properly open the details for each db file
                Agency a = new InstalledAgencyLoader(c, fileList[i]).getAgency();
                Log.d(TAG, "Found agency for " + fileList[i] + ", " + a);

                if (a != null) {
                    ret.add(a);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "No databases/ asset folder present");
            return null;
        }

        Log.i(TAG, "Found installed agencies: "+ret);
        return ret;
    }
}
