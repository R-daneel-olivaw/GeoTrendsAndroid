package aks.geotrends.android;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.db.Region;
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

        Log.d("geotrends_intentservice", "received intent");
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
//        throw new UnsupportedOperationException("Not yet implemented");

        Log.d("geotrends_intentservice", "refresh all visible...");

        SettingsDatasourceHelper sdh = new SettingsDatasourceHelper(this);
        sdh.open();
        final List<RegionsEnum> visibleRegions = sdh.getVisibleRegions();
        sdh.close();

        final Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                for (RegionsEnum reg : visibleRegions) {
                    regionalRefresh(reg.getCode());
                }
            }
        });
        workerThread.start();

        System.out.println(visibleRegions);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionQueryRegion(final int regionCode) {

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

    private synchronized void saveKeywordsToDatabase(JsonRegionalTrending regionalTrending) {

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
}
