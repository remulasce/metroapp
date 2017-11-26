package com.remulasce.lametroapp.static_data.hardcoded_hacks;

import com.remulasce.lametroapp.java_core.basic_types.Agency;

/**
 * Here's a place for agency names, strings, etc. that really should be a generic part of the scrape
 * process, but actually is done in the app with magic.
 */

public class HardcodedHacks {

    public static final int DATABASE_VERSION = 25;

    public static boolean useBay511(Agency agency) {
        return agencyMatchesAny(agency, "caltrain", "SamTrans", "VTA");
    }

    public static boolean useBart(Agency agency) {
        return agencyMatchesAny(agency, "BART");
    }

    public static boolean useNextrip(Agency agency) {
        return agencyMatchesAny(agency, "actransit", "sf-muni", "lametro", "lametro-rail");
    }

    private static boolean agencyMatchesAny(Agency agency, String... matchers) {
        for (String any : matchers) {
            if (agency.matches(any)) {
                return true;
            }
        }

        return false;
    }
}
