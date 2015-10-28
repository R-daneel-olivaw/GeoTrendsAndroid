package aks.geotrends.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aks.geotrends.android.utils.RegionsEnum;

public class SelectRegionsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView regionsRecyclerView;
    private RegionsAdapter adapter;
    private List<String> currentSelected;
    private List<RegionModel> regModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_regions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra("result", adapter.getSelected());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        regionsRecyclerView = (RecyclerView) findViewById(R.id.regionsRecyclerView);
        regionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        currentSelected = intent.getStringArrayListExtra("currentSelected");

        regModels = getRegionModels();
        adapter = new RegionsAdapter(this, regModels);
        regionsRecyclerView.setAdapter(adapter);
    }

    private List<RegionModel> getRegionModels() {

        List<RegionModel> regionModels = new ArrayList<>();
        List<RegionsEnum> regions = new ArrayList<>(Arrays.asList(RegionsEnum.values()));
        for (RegionsEnum reg : regions) {
            regionModels.add(new RegionModel(reg));
        }

        return regionModels;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_regionns_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<RegionModel> filteredModelList = filter(regModels, query);
        adapter.animateTo(filteredModelList);
        regionsRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<RegionModel> filter(List<RegionModel> models, String query) {
        query = query.toLowerCase();

        final List<RegionModel> filteredModelList = new ArrayList<>();
        for (RegionModel model : models) {
            final String printName = model.getRegionObject().getPrintName().toLowerCase();
            final String regionCode = model.getRegionObject().getRegion().toLowerCase();
            if (printName.contains(query)) {
                filteredModelList.add(model);
            } else if (regionCode.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private class RegionModel {

        private final String regionName;
        private final RegionsEnum regionObject;
        private boolean isChecked;

        public RegionModel(RegionsEnum region) {
            regionObject = region;
            regionName = regionObject.getPrintName();

            if (currentSelected.contains(region.getRegion())) {
                isChecked = true;
            } else {
                isChecked = false;
            }
        }

        public String getRegionName() {
            return regionName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public RegionsEnum getRegionObject() {
            return regionObject;
        }

        public void setChecked(boolean checked) {
            this.isChecked = checked;
        }
    }

    public class RegionViewHolder extends RecyclerView.ViewHolder {

        private final TextView regionName;
        private final CheckBox checkBox;

        public RegionViewHolder(View itemView) {
            super(itemView);

            regionName = (TextView) itemView.findViewById(R.id.regionName);
            checkBox = (CheckBox) itemView.findViewById(R.id.regionCheckBox);
        }

        public void bind(RegionModel model) {
            regionName.setText(model.getRegionName());
            checkBox.setChecked(model.isChecked());

            checkBox.setTag(model);
        }

        public void setCheckboxListner(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {

            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    private class RegionsAdapter extends RecyclerView.Adapter<RegionViewHolder> {

        private final LayoutInflater mInflater;
        private List<RegionModel> mModels;

        private View.OnClickListener checkBoxListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegionModel rm = (RegionModel) v.getTag();

                if (rm.isChecked()) {
                    rm.setChecked(false);
                } else {
                    rm.setChecked(true);
                }
            }
        };

        public RegionsAdapter(Context context, List<RegionModel> models) {
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public RegionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.region_recycler_item, parent, false);
            return new RegionViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RegionViewHolder holder, int position) {
            final RegionModel model = mModels.get(position);
            holder.bind(model);
            holder.checkBox.setOnClickListener(checkBoxListner);
            holder.checkBox.setChecked(model.isChecked);
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }

        public void setModels(List<RegionModel> models) {
            mModels = new ArrayList<>(models);
        }

        public ArrayList<String> getSelected() {
            ArrayList<String> selectedRegions = new ArrayList<>();
            for (RegionModel r : mModels) {
                if (r.isChecked()) {
                    selectedRegions.add(r.getRegionObject().getRegion());
                }
            }

            return selectedRegions;
        }

        public RegionModel removeItem(int position) {
            final RegionModel model = mModels.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, RegionModel model) {
            mModels.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final RegionModel model = mModels.remove(fromPosition);
            mModels.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<RegionModel> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<RegionModel> newModels) {
            for (int i = mModels.size() - 1; i >= 0; i--) {
                final RegionModel model = mModels.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<RegionModel> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final RegionModel model = newModels.get(i);
                if (!mModels.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<RegionModel> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final RegionModel model = newModels.get(toPosition);
                final int fromPosition = mModels.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }
}
