package aks.geotrends.android.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by a77kumar on 2015-11-08.
 */
public class BackgroundScheduler {

    public static void reScheduleSync(Context context) {
        final int minutes = SharedPreferenceHelper.getSyncPeriodMinutes(context);
        startScheduledIntents(minutes, context);

    }

    private static void startScheduledIntents(int minutes, Context context) {
        Intent intent = new Intent("aks.geotrends.android.action.query.visible");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), minToMillis(minutes), pendingIntent);

        Log.d("geotrends", "intents created");
    }

    private static long minToMillis(int minutes) {
        return 1000 * 60 * minutes;
    }

    private static boolean areIntentsScheduled(Context context) {
        boolean intentsUp = (PendingIntent.getBroadcast(context, 0,
                new Intent("aks.geotrends.android.action.query.visible"),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (intentsUp) {
            Log.d("geotrends", "intents are already active");
        }

        return intentsUp;
    }

    private static void cancellAllPendingIntents(Context context) {
        Intent intent = new Intent("aks.geotrends.android.action.query.visible");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        Log.d("geotrends", "intents cancelled");
    }

    public static void cancelAutoSync(Context context) {
        cancellAllPendingIntents(context);
    }
}
