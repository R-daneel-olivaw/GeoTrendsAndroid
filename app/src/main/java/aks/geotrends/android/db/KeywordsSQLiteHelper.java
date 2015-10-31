package aks.geotrends.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KeywordsSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_TRENDING_KEYWORDS = "trending_keywords";
	public static final String TABLE_REGIONS = "regions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_KEYWORD = "keyword";
	public static final String COLUMN_ADDED_DATE = "added_date";
	public static final String COLUMN_REGION = "region";
	public static final String COLUMN_REGION_SHORT = "region_short";
	public static final String COLUMN_REFRESHED_DATE = "refresh_date";
	public static final String COLUMN_FAVORRITE = "favorite";
	public static final String COLUMN_DISPLAYED_UI = "displayed";
	public static final String TABLE_REGIONAL_SETTINGS = "reg_settings";

	private static final String DATABASE_NAME = "keywords.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String CREATE_TABLE_KEYWORDS = "create table " + TABLE_TRENDING_KEYWORDS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_KEYWORD + " text not null," + COLUMN_ADDED_DATE
			+ " text not null," + COLUMN_REGION + " text not null);";
	private static final String CREATE_TABLE_REGIONS = "create table " + TABLE_REGIONS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_REGION_SHORT + " text not null, "+ COLUMN_REGION + " text not null);";
	private static final String CREATE_TABLE_REGIONAL_SETTINGS = "create table " + TABLE_REGIONAL_SETTINGS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_REGION_SHORT + " text not null," + COLUMN_REFRESHED_DATE
			+ " text not null," + COLUMN_FAVORRITE + " integer default 0, "+COLUMN_DISPLAYED_UI+" integer default 0);";

	public KeywordsSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_KEYWORDS);
		database.execSQL(CREATE_TABLE_REGIONS);
		database.execSQL(CREATE_TABLE_REGIONAL_SETTINGS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(KeywordsSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRENDING_KEYWORDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONAL_SETTINGS);
		onCreate(db);
	}

}