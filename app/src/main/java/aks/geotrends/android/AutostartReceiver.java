package aks.geotrends.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import aks.geotrends.android.utils.BackgroundScheduler;
import aks.geotrends.android.utils.SharedPreferenceHelper;

public class AutostartReceiver extends BroadcastReceiver {
    public AutostartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SharedPreferenceHelper.isAutoSyncEnabled(context)) {
            BackgroundScheduler.reScheduleSync(context);
            Log.d("Power-On", "Setting up intents");
        }
    }
}
