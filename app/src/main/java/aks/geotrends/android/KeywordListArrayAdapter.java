package aks.geotrends.android;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import aks.geotrends.android.db.Keyword;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeywordListArrayAdapter extends ArrayAdapter<Keyword> {

	private static final String LT_1_HOUR = "< 1 hour";

	private List<Keyword> objects;

	private PeriodFormatter daysHours = null;

	public KeywordListArrayAdapter(Context context, List<Keyword> objects) {
		super(context, R.layout.list_item, objects);
		this.objects = objects;

		daysHours = new PeriodFormatterBuilder().appendDays().appendSuffix(" day", " days").appendSeparator(" and ")
				.appendHours().appendSuffix(" hour", " hours").toFormatter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Keyword keyword = objects.get(position);

		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.list_item, parent, false);
			viewHolder.keyword = (TextView) convertView.findViewById(R.id.tvKeyword);
			viewHolder.date = (TextView) convertView.findViewById(R.id.tvDuration);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.keyword.setText(keyword.getKeyword());
		viewHolder.date.setText(getDurationText(keyword));

		return convertView;
	}

	private String getDurationText(Keyword keyword) {

		DateTime now = DateTime.now();
		DateTime added = new DateTime(keyword.getSortingDate());

		Interval keywordAge = new Interval(added, now);
		Period period = keywordAge.toPeriod();
		
		String keywordAgeString = null;
		if(period.toStandardMinutes().getMinutes()<60)
		{
			keywordAgeString = LT_1_HOUR;
		}
		else
		{
			keywordAgeString = daysHours.print(period);
		}

		return keywordAgeString;
	}

	// View lookup cache
	private static class ViewHolder {
		TextView keyword;
		TextView date;
	}

}
