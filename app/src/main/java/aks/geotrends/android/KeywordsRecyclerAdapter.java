package aks.geotrends.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;

import aks.geotrends.android.db.Keyword;

public class KeywordsRecyclerAdapter extends RecyclerView.Adapter<KeywordsRecyclerAdapter.ViewHolder> {

    private static final String LT_1_HOUR = "< 1h";

    private List<Keyword> objects;
    private View.OnClickListener clickHandler;

    private PeriodFormatter daysHours = null;

    public KeywordsRecyclerAdapter(List<Keyword> items, View.OnClickListener clickHandler) {
        objects = items;
        this.clickHandler = clickHandler;

        daysHours = new PeriodFormatterBuilder().appendWeeks().appendSuffix("w").appendDays().appendSuffix("d").appendHours().appendSuffix("h").toFormatter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        v.setOnClickListener(clickHandler);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Keyword keyword = objects.get(i);

        viewHolder.keyword.setText(keyword.getKeyword());
        viewHolder.date.setText(getDurationText(keyword));

        viewHolder.getItemView().setTag(keyword);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private String getDurationText(Keyword keyword) {

        DateTime now = DateTime.now();
        DateTime added = new DateTime(keyword.getSortingDate());

        Interval keywordAge = new Interval(added, now);
        Period period = keywordAge.toPeriod();

        String keywordAgeString;
        if (period.toStandardMinutes().getMinutes() < 60) {
            keywordAgeString = LT_1_HOUR;
        } else {
            keywordAgeString = daysHours.print(period);
        }

        return keywordAgeString;
    }

    // View lookup cache
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView keyword;
        private TextView date;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            keyword = (TextView) itemView.findViewById(R.id.tvKeyword);
            date = (TextView) itemView.findViewById(R.id.tvDuration);
        }

        public View getItemView() {
            return itemView;
        }
    }

}
