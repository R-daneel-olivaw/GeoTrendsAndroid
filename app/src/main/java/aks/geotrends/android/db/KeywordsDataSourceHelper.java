package aks.geotrends.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.text.TextUtils;
import android.util.Log;

public class KeywordsDataSourceHelper {

    private SQLiteDatabase database;
    private KeywordsSQLiteHelper dbHelper;
    private String[] KEYWORDS_ALL_COLUMNS = {KeywordsSQLiteHelper.COLUMN_ID, KeywordsSQLiteHelper.COLUMN_KEYWORD,
            KeywordsSQLiteHelper.COLUMN_REGION, KeywordsSQLiteHelper.COLUMN_ADDED_DATE};
    private String[] REGIONS_ALL_COLUMNS = {KeywordsSQLiteHelper.COLUMN_ID, KeywordsSQLiteHelper.COLUMN_REGION_SHORT,
            KeywordsSQLiteHelper.COLUMN_REGION};

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
        database = null;
    }

    public Keyword createKeyword(String keyword, String regionShort, String dateAdded) {

        Keyword newKeyword = null;
        synchronized (KeywordsDataSourceHelper.class) {

            ContentValues values = new ContentValues();
            values.put(KeywordsSQLiteHelper.COLUMN_KEYWORD, keyword);
            values.put(KeywordsSQLiteHelper.COLUMN_REGION, regionShort);
            values.put(KeywordsSQLiteHelper.COLUMN_ADDED_DATE, dateAdded);

            checkDatabase();
            long insertId = database.insert(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, null, values);
            Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, KEYWORDS_ALL_COLUMNS,
                    KeywordsSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            newKeyword = cursorToKeyword(cursor);
            cursor.close();
        }

        return newKeyword;
    }

    public List<String> saveOrUpdateKeywords(JsonRegionalTrending regionalTrending) {

        List<String> newKeywordsListStr = null;
        synchronized (KeywordsDataSourceHelper.class) {
            String regionShort = regionalTrending.getRegion().getRegion();

            final List<Keyword> oldKeywordsList = getKeywordsList(RegionsEnum.getRegionByShortCode(regionShort));
            deleteAllKeywordsForRegion(regionShort);

            List<JsonKeyword> trendingKeywords = regionalTrending.getTrending();

            final List<Keyword> newKeywordsList = new ArrayList<>();
            // save or update keywords one by one
            for (JsonKeyword jsonKeyword : trendingKeywords) {

                String iso8601Date = formatter.format(jsonKeyword.getAddedDate());
                final Keyword keyword = createKeyword(jsonKeyword.getKeyword(), regionShort, iso8601Date);
                newKeywordsList.add(keyword);
            }

            List<String> oldKeywordsStr = convertToStringList(oldKeywordsList);
            newKeywordsListStr = convertToStringList(newKeywordsList);

            newKeywordsListStr.removeAll(oldKeywordsStr);

            // notify content observers
            Uri uri = Uri.parse(KEYWORDS_TABLE_URI);
            context.getContentResolver().notifyChange(uri, null);
        }

        return newKeywordsListStr;
    }

    private List<String> convertToStringList(List<Keyword> oldKeywordsList) {

        List<String> strList = new ArrayList<>();

        for (Keyword k : oldKeywordsList) {
            strList.add(k.getKeyword());
        }

        return strList;
    }

    private void deleteAllKeywordsForRegion(String regionShort) {

        checkDatabase();
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
        synchronized (KeywordsDataSourceHelper.class) {
            String regionShort = region.getRegion();

            checkDatabase();
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
    }

    public Cursor getKeywordsCursor(RegionsEnum region) {
        Cursor cursor = null;
        synchronized (KeywordsDataSourceHelper.class) {
            checkDatabase();

            cursor = database.query(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, KEYWORDS_ALL_COLUMNS,
                    KeywordsSQLiteHelper.COLUMN_REGION + " = '" + region.getRegion() + "'", null, null, null, null);
        }
        return cursor;

    }

    private void checkDatabase() {

        if(null==database)
        {
            open();
        }
    }

    public List<Keyword> getKeywordsList(RegionsEnum reg) {

        List<Keyword> keywords = new ArrayList<Keyword>();
        Cursor cursor = getKeywordsCursor(reg);

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

        return keywords;
    }

    public void cleanUpOldRegions(ArrayList<Integer> displayedRegionCodes) {

        synchronized (KeywordsDataSourceHelper.class) {
            List<String> displayedRegions = new ArrayList<>();
            for (Integer regCode : displayedRegionCodes) {
                final RegionsEnum region = RegionsEnum.getRegionForCode(regCode);
                displayedRegions.add("'" + region.getRegion() + "'");
            }

            String selection = KeywordsSQLiteHelper.COLUMN_REGION + " NOT IN (" + TextUtils.join(", ", displayedRegions) + ")";
            Log.d("cleanup", selection);
            checkDatabase();
            database.delete(KeywordsSQLiteHelper.TABLE_TRENDING_KEYWORDS, selection, null);
        }
    }
}
