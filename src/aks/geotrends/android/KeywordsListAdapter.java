package aks.geotrends.android;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub

	}

}
