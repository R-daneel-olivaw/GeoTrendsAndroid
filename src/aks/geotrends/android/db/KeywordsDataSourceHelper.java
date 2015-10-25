package aks.geotrends.android.db;

import java.text.SimpleDateFormat;
import java.util.List;

import aks.geotrends.android.json.JsonKeyword;
import aks.geotrends.android.json.JsonRegion;
import aks.geotrends.android.json.JsonRegionalTrending;
import aks.geotrends.android.utils.RegionsEnum;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class KeywordsDataSourceHelper {

	private SQLiteDatabase database;
	private KeywordsSQLiteHelper dbHelper;
	private String[] KEYWORDS_ALL_COLUMNS = { KeywordsSQLiteHelper.COLUMN_ID, KeywordsSQLiteHelper.COLUMN_KEYWORD,
			KeywordsSQLiteHelper.COLUMN_REGION, KeywordsSQLiteHelper.COLUMN_ADDED_DATE };
	private String[] REGIONS_ALL_COLUMNS = { KeywordsSQLiteHelper.COLUMN_ID, KeywordsSQLiteHelper.COLUMN_REGION_SHORT,
			KeywordsSQLiteHelper.COLUMN_REGION };
	
	public static final String KEYWORDS_TABLE_URI = "content://aks.geotrends.android/" + KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
	private Context context;

	public KeywordsDataSourceHelper(Context context) {
		this.context = context;
		dbHelper = new KeywordsSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Keyword createKeyword(String keyword, String regionShort, String dateAdded) {
		ContentValues values = new ContentValues();
		values.put(KeywordsSQLiteHelper.COLUMN_KEYWORD, keyword);
		values.put(KeywordsSQLiteHelper.COLUMN_REGION, regionShort);
		values.put(KeywordsSQLiteHelper.COLUMN_ADDED_DATE, dateAdded);

		long insertId = database.insert(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, null, values);
		Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, KEYWORDS_ALL_COLUMNS,
				KeywordsSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Keyword newKeyword = cursorToKeyword(cursor);
		cursor.close();
		
		return newKeyword;
	}

	public void saveOrUpdateKeywords(JsonRegionalTrending regionalTrending) {
		String regionShort = regionalTrending.getRegion().getRegion();
		deleteAllKeywordsForRegion(regionShort);

		List<JsonKeyword> trendingKeywords = regionalTrending.getTrending();

		// save or update keywords one by one
		for (JsonKeyword jsonKeyword : trendingKeywords) {

			String iso8601Date = formatter.format(jsonKeyword.getAddedDate());
			createKeyword(jsonKeyword.getKeyword(), regionShort, iso8601Date);
		}
		
		// notify content observers
		Uri uri = Uri.parse(KEYWORDS_TABLE_URI);
		context.getContentResolver().notifyChange(uri, null);
	}

	private void deleteAllKeywordsForRegion(String regionShort) {


		database.delete(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS,
				KeywordsSQLiteHelper.COLUMN_REGION + " = '" + regionShort + "'", null);

	}

	private Keyword cursorToKeyword(Cursor cursor) {
		Keyword keyword = new Keyword();
		keyword.setId(cursor.getLong(0));
		keyword.setKeyword(cursor.getString(1));
		keyword.setRegionShort(cursor.getString(2));
		return keyword;
	}

	public void saveRegion(JsonRegion region) {

		String regionShort = region.getRegion();

		Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_REGIONS, REGIONS_ALL_COLUMNS,
				KeywordsSQLiteHelper.COLUMN_REGION_SHORT + " = '" + regionShort + "'", null, null, null, null);

		cursor.moveToFirst();
		if (cursor.getCount() != 0) {
			cursor.close();
		} else {
			cursor.close();

			RegionsEnum regionObj = RegionsEnum.getRegionByShortCode(regionShort);

			ContentValues values = new ContentValues();
			values.put(KeywordsSQLiteHelper.COLUMN_REGION_SHORT, regionShort);
			values.put(KeywordsSQLiteHelper.COLUMN_REGION, regionObj.getPrintName());

			database.insert(KeywordsSQLiteHelper.TABLE_REGIONS, null, values);
		}

	}

	public Cursor getKeywords(RegionsEnum region) {
		// TODO Auto-generated method stub
		
		Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, KEYWORDS_ALL_COLUMNS,
				KeywordsSQLiteHelper.COLUMN_REGION + " = '" + region.getRegion() + "'", null, null, null, null);
		
		return cursor;
	}
}
