package aks.geotrends.android;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.WeakHashMap;

import aks.geotrends.android.db.KeywordsDataSourceHelper;
import aks.geotrends.android.fragments.KeywordRecyclerViewFragment;
import aks.geotrends.android.utils.RegionsEnum;


public class MainActivity extends AppCompatActivity {

    private final WeakHashMap<RegionsEnum, Fragment> fragmentWeakMap = new WeakHashMap<RegionsEnum, Fragment>();

    private DrawerLayout mDrawerLayout;
    private KeywordsContentObserver keywordContentObserver;
    private ViewPager viewPager;
    private RegionsPagerAdapter adapter;
    private List<RegionsEnum> regions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.about_app:
                        Intent i = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.feedback:
                        openFeedbackForm();
                        return true;
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshDatabase();

                Snackbar.make(findViewById(R.id.coordinator), "Refreshing list ..", Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//						Toast.makeText(MainActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });
    }

    private void openFeedbackForm() {

        String url = "http://goo.gl/forms/hWyj6jD9X6";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new RegionsPagerAdapter(getSupportFragmentManager(),regions);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        String uriString = KeywordsDataSourceHelper.KEYWORDS_TABLE_URI;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_select_regions:
                startSelectRegionsActivity();
                return true;
//            case R.id.action_reorder:
//                return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> selectedRegionsCodes = data.getStringArrayListExtra("result");
                System.out.println(selectedRegionsCodes);

                regions = new ArrayList<RegionsEnum>();
                for (String regCode:selectedRegionsCodes) {

                    final RegionsEnum region = RegionsEnum.getRegionByShortCode(regCode);
                    regions.add(region);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void startSelectRegionsActivity() {

        final List<RegionsEnum> regionsEnumList = adapter.getRegionList();
        ArrayList<String> regCodeList = new ArrayList<>();

        for (RegionsEnum rEnum: regionsEnumList) {

            regCodeList.add(rEnum.getRegion());
        }

        final Intent intent = new Intent(this, SelectRegionsActivity.class);
        intent.putStringArrayListExtra("currentSelected", regCodeList);
        startActivityForResult(intent, 1);
    }

    private Fragment getFragmentForRegion(RegionsEnum region, int position) {
        Fragment fragment = fragmentWeakMap.get(region);
        if (fragment == null) {
            fragment = KeywordRecyclerViewFragment.newInstance(region, position);
            fragmentWeakMap.put(region, fragment);
        }

        return fragment;
    }

    private void refreshDatabase() {

        final Fragment fragment = adapter.getCurrentFragment();
        if (fragment instanceof KeywordRecyclerViewFragment) {
            KeywordRecyclerViewFragment klFragment = (KeywordRecyclerViewFragment) fragment;
            klFragment.startDelayedRefresh();
        }

    }

    private class RegionsPagerAdapter extends FragmentStatePagerAdapter {

        private final RegionsEnum[] regionsArray = {RegionsEnum.UnitedStates, RegionsEnum.India, RegionsEnum.Japan, RegionsEnum.Ukraine, RegionsEnum.Brazil, RegionsEnum.Egypt, RegionsEnum.Canada};
        private List<RegionsEnum> regionList;

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public RegionsPagerAdapter(FragmentManager fm, List<RegionsEnum> regions) {
            super(fm);
            if(regions==null || regions.size()==0) {
                regionList = new ArrayList<>();
                regionList.addAll(Arrays.asList(regionsArray));
            }
            else
            {
                regionList = regions;
            }
        }

        public  List<RegionsEnum> getRegionList()
        {
            return  regionList;
        }

        @Override
        public Fragment getItem(int position) {

            final RegionsEnum reg = regionList.get(position);
            final Fragment fragment = getFragmentForRegion(reg, position);

            return fragment;
        }

        @Override
        public int getCount() {
            return regionList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return regionList.get(position).getPrintName();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
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

            final Collection<Fragment> fragmentCollection = fragmentWeakMap.values();
            for (Fragment f : fragmentCollection) {
                if (f instanceof KeywordRecyclerViewFragment) {
                    KeywordRecyclerViewFragment klFrag = (KeywordRecyclerViewFragment) f;
                    klFrag.databaseContentsChanged();
                }
            }

            viewPager.invalidate();
        }

    }

}
