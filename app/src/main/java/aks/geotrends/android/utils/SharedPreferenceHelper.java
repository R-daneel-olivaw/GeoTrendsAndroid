package aks.geotrends.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

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

    public static int getSyncPeriodMinutes(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final String syncFrequencyString = prefs.getString("sync_frequency", "30");
        final int sync_frequency = Integer.parseInt(syncFrequencyString);

        return sync_frequency;
    }

    public static boolean isAutoSyncEnabled(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean sync = prefs.getBoolean("background_sync", true);

        return sync;
    }

    public static boolean isNotificationEnabled(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean notification = prefs.getBoolean("notifications_enabled", true);

        return notification;
    }

    public static boolean isVibrationEnabled(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean vibrate = prefs.getBoolean("notifications_vibrate", true);

        return vibrate;
    }

    public static Uri getRingtone(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String ringtone = prefs.getString("notifications_new_message_ringtone", null);
        if(null==ringtone)
        {
            return null;
        }
        final Uri ringtoneUri = Uri.parse(ringtone);

        return ringtoneUri;
    }
}
