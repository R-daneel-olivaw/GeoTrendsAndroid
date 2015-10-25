package aks.geotrends.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.json.JsonKeyword;
import aks.geotrends.android.json.JsonRegionalTrending;
import aks.geotrends.android.utils.RegionsEnum;
import aks.geotrends.android.utils.WebserviceHelper;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GeoTrendsService extends Service {

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			int regIntCode = msg.getData().getInt("reg", -1);
			RegionsEnum region = RegionsEnum.getRegionForCode(regIntCode);

			if (region == null) {
				Log.e("Region not found", "Region int code = " + regIntCode);
			} else {
				try {

					WebserviceHelper weHelper = new WebserviceHelper();
					JsonRegionalTrending regionalTrending = weHelper.fetchKeyowrdForRegion(region);

					System.out.println(regionalTrending);

					saveKeywordsToDatabase(regionalTrending);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments");
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	private void saveKeywordsToDatabase(JsonRegionalTrending regionalTrending) {

		KeywordsDataSourceHelper helper = new KeywordsDataSourceHelper(getApplicationContext());
		helper.open();
		
		helper.saveRegion(regionalTrending.getRegion());
		helper.saveOrUpdateKeywords(regionalTrending);
		
		helper.close();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		int regIntCode = intent.getIntExtra("reg", -1);

		Bundle b = new Bundle();
		b.putInt("reg", regIntCode);

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.setData(b);
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
