package aks.geotrends.android;

import java.util.WeakHashMap;

import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.fragments.KeywordListFragment;
import aks.geotrends.android.utils.RegionsEnum;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private final WeakHashMap<RegionsEnum, Fragment> fragmentWeakMap = new WeakHashMap<RegionsEnum, Fragment>();

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

	private KeywordsContentObserver keywordContentObserver;

	private Fragment visibleFragment;

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
	protected void onResume() {
		super.onResume();

		String uriString = "content://aks.geotrends.android/" + KeywordsDataSourceHelper.TABLE_TRENDING_KEYWORDS;
		Uri uri = Uri.parse(uriString);

		keywordContentObserver = new KeywordsContentObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true, keywordContentObserver);
	}

	@Override
	protected void onPause() {
		super.onPause();

		getContentResolver().unregisterContentObserver(keywordContentObserver);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		position++;

		switch (position) {
		case 1:
			visibleFragment = getFragmentForRegion(RegionsEnum.UnitedStates, position);
			break;
		case 2:
			visibleFragment = getFragmentForRegion(RegionsEnum.India, position);
			break;
		case 3:
			visibleFragment = getFragmentForRegion(RegionsEnum.Japan, position);
			break;

		default:
			visibleFragment = getFragmentForRegion(RegionsEnum.UnitedKingdom, position);
			break;
		}

		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, visibleFragment).commit();
	}

	public void onSectionAttached(int number) {

		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);

			break;
		case 2:
			mTitle = getString(R.string.title_section2);

			break;
		case 3:
			mTitle = getString(R.string.title_section3);

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

	private Fragment getFragmentForRegion(RegionsEnum region, int position) {
		Fragment fragment = fragmentWeakMap.get(region);
		if (fragment == null) {
			fragment = KeywordListFragment.newInstance(region, position);
			fragmentWeakMap.put(region, fragment);
		}

		return fragment;
	}


	private class KeywordsContentObserver extends ContentObserver {

		public KeywordsContentObserver(Handler handler) {
			super(handler);
		}

		// Implement the onChange(boolean) method to delegate the change
		// notification to
		// the onChange(boolean, Uri) method to ensure correct operation on
		// older versions
		// of the framework that did not have the onChange(boolean, Uri) method.
		@Override
		public void onChange(boolean selfChange) {
			onChange(selfChange, null);
		}

		// Implement the onChange(boolean, Uri) method to take advantage of the
		// new Uri argument.
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// Handle change.

			System.out.println("CONTENT CHANGED !!!!!");
			
			if(visibleFragment instanceof KeywordListFragment)
			{
				KeywordListFragment klFrag = (KeywordListFragment) visibleFragment;
				klFrag.databaseContentsChanged();
			}
		}

	}
}
