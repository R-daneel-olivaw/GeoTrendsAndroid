package aks.geotrends.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by a77kumar on 2015-11-05.
 */
public class SharedPreferenceHelper {

    private static final String SHARED_PREFS_FILE = "googligencepref";
    private static final String REGIONS_SET = "regionsSet";
    private static final String CURRENT_REGION = "current_region";

    public static List<RegionsEnum> fetchDisplayedRegionsFromSharedPrefrences(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        final Set<String> regionCodeSet = prefs.getStringSet(REGIONS_SET, null);

        if (null == regionCodeSet || regionCodeSet.size() == 0) {
            return null;
        } else {
            List<RegionsEnum> regionsList = new ArrayList<>();

            for (String regCode : regionCodeSet) {

                regionsList.add(RegionsEnum.getRegionByShortCode(regCode));
            }

            return regionsList;
        }
    }
}
