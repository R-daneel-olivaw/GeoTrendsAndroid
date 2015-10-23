package aks.geotrends.android.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import aks.geotrends.android.KeywordListArrayAdapter;
import aks.geotrends.android.MainActivity;
import aks.geotrends.android.R;
import aks.geotrends.android.db.Keyword;
import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.db.KeywordsSQLiteHelper;
import aks.geotrends.android.utils.RegionsEnum;
import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class KeywordListFragment extends ListFragment {
	/*
	 * The fragment argument representing the section number for this fragment.
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

				String keyword = cursor.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_KEYWORD));
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
		
		Collections.sort(keywords, new KeywordComparator());

		return keywords;
	}

	private Cursor getDataCursor() {

		KeywordsDataSourceHelper helper = new KeywordsDataSourceHelper(getActivity());
		Cursor c = helper.getKeywords(region);

		return c;
	}

	private class KeywordComparator implements Comparator<Keyword> {

		@Override
		public int compare(Keyword lhs, Keyword rhs) {
			// TODO Auto-generated method stub
			
			if(lhs.getSortingDate().before(rhs.getSortingDate()))
			{
				return -1;
			}
			else if(lhs.getSortingDate().equals(rhs.getSortingDate()))
			{
				return 0;
			}
			
			return 1;
		}

	}
}