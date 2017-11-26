package com.remulasce.lametroapp.static_data;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.remulasce.lametroapp.java_core.analytics.Log;
import com.remulasce.lametroapp.java_core.basic_types.Agency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    /*
     * Agency -> the sqlite db file.
     *
     * Eg. {VTA} -> vta.db
     */
    private Map<Agency, String> agencyFileNames = new HashMap<Agency, String>();

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
                String fileName = fileList[i];

                // We need to properly open the details for each db file
                Agency a = new InstalledAgencyLoader(c, fileName).getAgency();

                Log.d(TAG, "Found agency for " + fileName + ", " + a);

                if (a != null) {
                    ret.add(a);
                    agencyFileNames.put(a, fileName);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "No databases/ asset folder present");
            return null;
        }

        Log.i(TAG, "Found installed agencies: "+ret);
        return ret;
    }

    /** For a given agency, get the name of the .db file that represents it. */
    public String getDatabaseFileNameForAgency(Agency agency) {
        return agencyFileNames.get(agency);
    }
}
