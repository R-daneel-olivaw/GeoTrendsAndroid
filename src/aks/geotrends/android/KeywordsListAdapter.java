package aks.geotrends.android;

import aks.geotrends.android.db.KeywordsSQLiteHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class KeywordsListAdapter extends CursorAdapter {

	private Context context;
	private Cursor c;

	public KeywordsListAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.c = c;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
	      // Find fields to populate in inflated template
	      TextView tvKeyword = (TextView) view.findViewById(R.id.tvKeyword);
	      TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
	      
	      // Extract properties from cursor
	      String keyword = cursor.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_KEYWORD));
	      String addedDate = cursor.getString(cursor.getColumnIndexOrThrow(KeywordsSQLiteHelper.COLUMN_ADDED_DATE));
	      
	      // Set the text of textviews
	      tvKeyword.setText(keyword);
	      tvDate.setText(addedDate);

	}

}
