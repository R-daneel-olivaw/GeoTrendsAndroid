package aks.geotrends.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import aks.geotrends.android.GeoTrendsService;
import aks.geotrends.android.KeywordListArrayAdapter;
import aks.geotrends.android.KeywordsRecyclerAdapter;
import aks.geotrends.android.MainActivity;
import aks.geotrends.android.R;
import aks.geotrends.android.db.Keyword;
import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.db.KeywordsSQLiteHelper;
import aks.geotrends.android.utils.RegionsEnum;

public class KeywordRecyclerViewFragment extends Fragment {
    /*
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RegionsEnum region;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
    private View view;
    private MainActivity activity;
    private KeywordsDataSourceHelper helper;
    private RecyclerView recyclerView;

    public static KeywordRecyclerViewFragment newInstance(RegionsEnum region, int sectionNumber) {
        KeywordRecyclerViewFragment fragment = new KeywordRecyclerViewFragment(region);

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private KeywordRecyclerViewFragment(RegionsEnum region) {
        this.region = region;
    }

    private KeywordRecyclerViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.keyword_recycler_fragment, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        helper = new KeywordsDataSourceHelper(activity);
        helper.open();
        populateRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.close();
        helper = null;
    }

    public void startDelayedRefresh() {
        refreshDatabase();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                if (mSwipeRefreshLayout.isRefreshing()) {
//                    mSwipeRefreshLayout.setRefreshing(false);
//                }
//
//            }
//        }, 5000);
    }

    private void populateRecyclerView() {

        Cursor c = getDataCursor();

        if (c.getCount() == 0) {
            startDelayedRefresh();
        }

        List<Keyword> keywords = getKeywordsFromCursor(c);
        recyclerView.setAdapter(new KeywordsRecyclerAdapter(keywords));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (MainActivity) activity;
//		(this.activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));

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

    private void refreshDatabase() {

        Intent serviceIntent = new Intent(activity, GeoTrendsService.class);
        serviceIntent.putExtra("reg", region.getCode());
        activity.startService(serviceIntent);

    }

    private Cursor getDataCursor() {

        Cursor c = helper.getKeywords(region);

        return c;
    }

    private class KeywordComparator implements Comparator<Keyword> {

        @Override
        public int compare(Keyword lhs, Keyword rhs) {

            if (lhs.getSortingDate().before(rhs.getSortingDate())) {
                return -1;
            } else if (lhs.getSortingDate().equals(rhs.getSortingDate())) {
                return 0;
            }

            return 1;
        }

    }

    public void databaseContentsChanged() {
        if(null!=helper) {
            populateRecyclerView();
        }
    }
}