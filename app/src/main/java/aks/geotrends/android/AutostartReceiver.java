package aks.geotrends.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import aks.geotrends.android.utils.BackgroundScheduler;

public class AutostartReceiver extends BroadcastReceiver {
    public AutostartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundScheduler.reScheduleSync(context);
        Log.d("Power-On","Setting up intents");
    }
}
