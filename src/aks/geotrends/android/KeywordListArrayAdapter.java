package aks.geotrends.android;
import java.util.List;

import aks.geotrends.android.db.Keyword;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeywordListArrayAdapter extends ArrayAdapter<Keyword> {

	private List<Keyword> objects;

	public KeywordListArrayAdapter(Context context, List<Keyword> objects) {
		super(context, R.layout.list_item, objects);
		this.objects = objects;
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
	          viewHolder.date = (TextView) convertView.findViewById(R.id.tvDate);
	          convertView.setTag(viewHolder);
	       } else {
	           viewHolder = (ViewHolder) convertView.getTag();
	       }
	       
	       viewHolder.keyword.setText(keyword.getKeyword());
	       viewHolder.date.setText(keyword.getAddedDate());
		
		return convertView;
	}
	
    // View lookup cache
    private static class ViewHolder {
        TextView keyword;
        TextView date;
    }

}
