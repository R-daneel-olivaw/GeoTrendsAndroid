package aks.geotrends.android;

import aks.geotrends.android.fragments.KeywordListFragment;
import aks.geotrends.android.utils.RegionsEnum;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		position++;

		Fragment fragment = null;
		switch (position) {
		case 1:
			fragment = KeywordListFragment.newInstance(RegionsEnum.UnitedStates, (position));
			break;
		case 2:
			fragment = KeywordListFragment.newInstance(RegionsEnum.India, (position));
			break;
		case 3:
			fragment = KeywordListFragment.newInstance(RegionsEnum.Japan, (position));
			break;

		default:
			fragment = KeywordListFragment.newInstance(RegionsEnum.UnitedKingdom, (position));
			break;
		}

		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
	}

	public void onSectionAttached(int number) {

		Intent serviceIntent = null;
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);

			serviceIntent = new Intent(MainActivity.this, GeoTrendsService.class);
			serviceIntent.putExtra("reg", RegionsEnum.UnitedStates.getCode());
			startService(serviceIntent);

			break;
		case 2:
			mTitle = getString(R.string.title_section2);

			serviceIntent = new Intent(MainActivity.this, GeoTrendsService.class);
			serviceIntent.putExtra("reg", RegionsEnum.India.getCode());
			startService(serviceIntent);

			break;
		case 3:
			mTitle = getString(R.string.title_section3);

			serviceIntent = new Intent(MainActivity.this, GeoTrendsService.class);
			serviceIntent.putExtra("reg", RegionsEnum.Japan.getCode());
			startService(serviceIntent);

			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
