package aks.geotrends.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import aks.geotrends.android.utils.RegionsEnum;

/**
 * Created by a77kumar on 2015-10-31.
 */
public class SettingsDatasourceHelper {

    private SQLiteDatabase database;
    private KeywordsSQLiteHelper dbHelper;

    private String[] REGIONAL_SETTINGS_ALL_COLUMNS = {KeywordsSQLiteHelper.COLUMN_ID, KeywordsSQLiteHelper.COLUMN_REGION_SHORT,
            KeywordsSQLiteHelper.COLUMN_REFRESHED_DATE, KeywordsSQLiteHelper.COLUMN_FAVORRITE, KeywordsSQLiteHelper.COLUMN_DISPLAYED_UI};

    public static final String REGIONAL_SETTINGS_TABLE_URI = "content://aks.geotrends.android/" + KeywordsSQLiteHelper.TABLE_REGIONAL_SETTINGS;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
    private Context context;

    public SettingsDatasourceHelper(Context context) {
        this.context = context;
        dbHelper = new KeywordsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public RegionalSettings createSettingsForRegion(RegionsEnum region) {
        ContentValues values = new ContentValues();
        values.put(KeywordsSQLiteHelper.COLUMN_REGION_SHORT, region.getRegion());
        values.put(KeywordsSQLiteHelper.COLUMN_REFRESHED_DATE, getDateFormatted(new Date(0)));
        values.put(KeywordsSQLiteHelper.COLUMN_FAVORRITE, 0);
        values.put(KeywordsSQLiteHelper.COLUMN_DISPLAYED_UI, 0);

        long insertId = database.insert(KeywordsSQLiteHelper.TABLE_REGIONAL_SETTINGS, null, values);
        Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_REGIONAL_SETTINGS, REGIONAL_SETTINGS_ALL_COLUMNS,
                KeywordsSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        RegionalSettings rSettings = getSettings(cursor);

        return rSettings;
    }

    private String getDateFormatted(Date date) {
        return formatter.format(date);
    }

    private RegionalSettings getSettings(Cursor cursor) {

        RegionalSettings rSettings = new RegionalSettings();

        rSettings.setId(cursor.getLong(0));
        rSettings.setRegionCode(cursor.getString(1));
        final String stringDate = cursor.getString(2);
        try {
            rSettings.setRefreshDate(formatter.parse(stringDate));
        } catch (ParseException e) {
            e.printStackTrace();
            rSettings.setRefreshDate(new Date(0));
        }
        rSettings.setIsFavorite(true ? cursor.getInt(3) == 1 : false);
        rSettings.setIsDisplayed(true ? cursor.getInt(4) == 1 : false);

        return rSettings;
    }

    private String getDateFormattedNow() {

        return getDateFormatted(new Date());
    }

    public void ensureAllRegions() {
        final RegionsEnum[] allRegions = RegionsEnum.values();

        for (RegionsEnum reg : allRegions) {

            RegionalSettings rSetting = getSettingsForRegion(reg);
            if (null == rSetting) {
                createSettingsForRegion(reg);
            }
        }
    }

    public RegionalSettings getSettingsForRegion(RegionsEnum reg) {

        RegionalSettings rSettings = null;

        Cursor cursor = database.query(KeywordsSQLiteHelper.TABLE_REGIONAL_SETTINGS, REGIONAL_SETTINGS_ALL_COLUMNS,
                KeywordsSQLiteHelper.COLUMN_REGION_SHORT + " = '" + reg.getRegion() + "'", null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            rSettings = getSettings(cursor);
        }
        cursor.close();

        return rSettings;
    }


    public void updateRefreshedDate(RegionsEnum region, Date refreshedDate) {

        final RegionalSettings settingsForRegion = getSettingsForRegion(region);
        settingsForRegion.setRefreshDate(refreshedDate);

        updateSettingsForRegion(settingsForRegion);
    }

    private void updateSettingsForRegion(RegionalSettings settingsForRegion) {

        ContentValues values = new ContentValues();
        values.put(KeywordsSQLiteHelper.COLUMN_REGION_SHORT, settingsForRegion.getRegionCode());
        values.put(KeywordsSQLiteHelper.COLUMN_REFRESHED_DATE, getDateFormatted(settingsForRegion.getRefreshDate()));

        int favorite = 0;
        if (settingsForRegion.isFavorite()) {
            favorite = 1;
        }
        int displayed = 0;
        if (settingsForRegion.isDisplayed()) {
            displayed = 1;
        }

        values.put(KeywordsSQLiteHelper.COLUMN_FAVORRITE, favorite);
        values.put(KeywordsSQLiteHelper.COLUMN_DISPLAYED_UI, displayed);

        database.update(KeywordsSQLiteHelper.TABLE_REGIONAL_SETTINGS, values,"_id "+"="+settingsForRegion.getId(),null);
    }
}
