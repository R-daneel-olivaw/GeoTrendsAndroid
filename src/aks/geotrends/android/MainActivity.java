package aks.geotrends.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aks.geotrends.android.db.Keyword;
import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.db.KeywordsSQLiteHelper;
import aks.geotrends.android.utils.RegionsEnum;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
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
			fragment = PlaceholderFragment.newInstance(position + 1);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	public static class KeywordListFragment extends ListFragment {
		/*
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private RegionsEnum region;
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

		public static KeywordListFragment newInstance(RegionsEnum region, int sectionNumber) {
			KeywordListFragment fragment = new KeywordListFragment(region);

			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		private KeywordListFragment(RegionsEnum region) {
			this.region = region;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.keyword_list_fragment, container, false);
			return view;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));

			Cursor c = getDataCursor();
			List<Keyword> keywords = getKeywordsFromCursor(c);
			KeywordListArrayAdapter arrayAdapter = new KeywordListArrayAdapter(getActivity(), keywords);

			setListAdapter(arrayAdapter);
		}

		private List<Keyword> getKeywordsFromCursor(Cursor cursor) {

			List<Keyword> keywords = new ArrayList<Keyword>();
			try {
				while (cursor.moveToNext()) {

					String keyword = cursor
							.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_KEYWORD));
					String region = cursor.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_REGION));
					String addedDate = cursor
							.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_ADDED_DATE));

					Keyword k = new Keyword();
					k.setKeyword(keyword);
					k.setRegionShort(region);
					k.setAddedDate(addedDate);

					try {
						Date date = formatter.parse(addedDate);
						k.setSortingDate(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					keywords.add(k);
				}

			} finally {
				cursor.close();
			}

			return keywords;
		}

		private Cursor getDataCursor() {

			KeywordsDataSourceHelper helper = new KeywordsDataSourceHelper(getActivity());
			Cursor c = helper.getKeywords(region);

			return c;
		}
	}

}
