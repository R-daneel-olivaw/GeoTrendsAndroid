package aks.geotrends.android;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.db.SettingsDatasourceHelper;
import aks.geotrends.android.json.JsonRegionalTrending;
import aks.geotrends.android.utils.RegionsEnum;
import aks.geotrends.android.utils.WebserviceHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoTrendsIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_AKS_GEOTRENDS_ANDROID_ACTION_QUERY_VISIBLE = "aks.geotrends.android.action.query.visible";
    private static final String AKS_GEOTRENDS_ANDROID_ACTION_QUERY_REGION = "aks.geotrends.android.action.query.region";

    // TODO: Rename parameters
    private static final String REGION = "aks.geotrends.android.extra.region";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GeoTrendsIntentService.class);
        intent.setAction(ACTION_AKS_GEOTRENDS_ANDROID_ACTION_QUERY_VISIBLE);
        intent.putExtra(REGION, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GeoTrendsIntentService.class);
        intent.setAction(AKS_GEOTRENDS_ANDROID_ACTION_QUERY_REGION);
        intent.putExtra(REGION, param1);
        context.startService(intent);
    }

    public GeoTrendsIntentService() {
        super("GeoTrendsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_AKS_GEOTRENDS_ANDROID_ACTION_QUERY_VISIBLE.equals(action)) {
                handleActionQueryVisible();
            } else if (AKS_GEOTRENDS_ANDROID_ACTION_QUERY_REGION.equals(action)) {
                final int regionCode = intent.getIntExtra(REGION, -1);
                handleActionQueryRegion(regionCode);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionQueryVisible() {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionQueryRegion(final int regionCode) {
        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");

        final Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                regionalRefresh(regionCode);

            }
        });
        workerThread.start();
    }

    private void regionalRefresh(int regIntCode) {
        RegionsEnum region = RegionsEnum.getRegionForCode(regIntCode);

        if (region == null) {
            Log.e("Region not found", "Region int code = " + regIntCode);
        } else {
            try {

                WebserviceHelper weHelper = new WebserviceHelper();
                JsonRegionalTrending regionalTrending = weHelper.fetchKeyowrdForRegion(region);

                System.out.println(regionalTrending);

                saveKeywordsToDatabase(regionalTrending);

                updateRefreshDate(region);

            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(GeoTrendsIntentService.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveKeywordsToDatabase(JsonRegionalTrending regionalTrending) {

        KeywordsDataSourceHelper helper = new KeywordsDataSourceHelper(getApplicationContext());
        helper.open();

        helper.saveRegion(regionalTrending.getRegion());
        helper.saveOrUpdateKeywords(regionalTrending);

        helper.close();
    }

    private void updateRefreshDate(RegionsEnum region) {

        SettingsDatasourceHelper settingsHelper = new SettingsDatasourceHelper(this);
        settingsHelper.open();
        settingsHelper.updateRefreshedDate(region, new Date());
        settingsHelper.close();
    }

    private void startScheduledIntents() {
        Intent intent = new Intent("aks.geotrends.android.ws.query.visible");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);

        Log.d("geotrends", "intents created");
    }

    private boolean areIntentsScheduled() {
        boolean intentsUp = (PendingIntent.getBroadcast(this, 0,
                new Intent("aks.geotrends.android.ws.query.visible"),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (intentsUp) {
            Log.d("geotrends", "intents are already active");
        }

        return intentsUp;
    }

    private void cancellAllPendingIntents(String action) {
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        Log.d("geotrends", "intents cancelled");
    }
}
