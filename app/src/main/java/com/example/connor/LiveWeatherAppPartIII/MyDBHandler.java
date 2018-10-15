package com.example.connor.LiveWeatherAppPartIII;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
/**
 * Created by Connor on 11/3/2016.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "liveWeather.db";

    // Cities table name
    private static final String TABLE_CITIES = "cities";

    // Cities Table Columns
    // Just save the data that's going to be the same between sessions
    // TODO: maybe add everything else?
    private static final String KEY_ZIP = "zip";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "state";
    private static final String KEY_WEATHER_DESC = "weatherDesc";
    private static final String KEY_WEATHER_TYPE_ICON = "weatherTypeIcon";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_TEMP_HIGH = "tempHigh";
    private static final String KEY_TEMP_LOW = "tempLow";

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CITIES + "("
                + KEY_ZIP + " TEXT PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_STATE + " TEXT"
                + ")";
        /*String CREATE_CONTACTS_TABLE = "CREATE TABLE " + "("
                + KEY_ZIP + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_STATE + " TEXT, "
                + KEY_TEMP + " TEXT, "
                + KEY_TEMP_HIGH + " TEXT, "
                + KEY_TEMP_LOW + " TEXT, "
                + KEY_WEATHER_TYPE_ICON + " TEXT, "
                + KEY_WEATHER_DESC + " TEXT"
                + ")";*/
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        onCreate(db);
    }

    public boolean cf_initRows() {
        if(getCitiesCount() == 0) {
            SQLiteDatabase lv_db = this.getWritableDatabase();

            lv_db.execSQL("INSERT INTO " + TABLE_CITIES + " VALUES('48197', 'Ypsilanti', 'Michigan')");
            lv_db.execSQL("INSERT INTO " + TABLE_CITIES + " VALUES('85365', 'Fortuna Foothills', 'Arizona')");
            lv_db.execSQL("INSERT INTO " + TABLE_CITIES + " VALUES('99703', 'Fairbanks', 'Alaska')");

            lv_db.close();
            return true;
        }
        else {
            return false;
        }
    }

    // Adding new contact
    public void addCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ZIP, city.getCityZip());
        values.put(KEY_NAME, city.getCityName());
        values.put(KEY_STATE, city.getCityState());
        /*values.put(KEY_TEMP, city.getCityTemp());
        values.put(KEY_TEMP_HIGH, city.getCityTempHigh());
        values.put(KEY_TEMP_LOW, city.getCityTempLow());
        values.put(KEY_WEATHER_TYPE_ICON, city.getCityWeatherTypeIcon());
        values.put(KEY_WEATHER_DESC, city.getCityWeatherDesc());*/
        db.insertOrThrow(TABLE_CITIES, null, values);
        db.close();
    }

    //check if a city already exists in the database based (based on zip)
    public boolean hasCity(City city){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + TABLE_CITIES + " WHERE " + KEY_ZIP + " = " + city.getCityZip();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    //check if a ZIP already exists in the database
    public boolean hasCityZip(String zip){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + TABLE_CITIES + " WHERE " + KEY_ZIP + " = " + zip;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }



    // Getting single city
    public City getCity(int zip) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CITIES, new String[] {
                    KEY_ZIP,
                    KEY_NAME,
                    KEY_STATE/*,
                    KEY_TEMP,
                    KEY_TEMP_HIGH,
                    KEY_TEMP_LOW,
                    KEY_WEATHER_TYPE_ICON,
                    KEY_WEATHER_DESC*/
                },
                KEY_ZIP + "=?",
                new String[] { String.valueOf(zip) }, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }

        City city = new City(cursor.getString(0), cursor.getString(1), cursor.getString(2));

        return city;
    }

    // Getting All Cities
    public ArrayList<City> getAllCities() {
        ArrayList<City> lv_myList = new ArrayList<City>();
        String selectQuery = "SELECT * FROM " + TABLE_CITIES;
        SQLiteDatabase lv_db = this.getReadableDatabase();
        Cursor cursor = lv_db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                City data = new City();
                data.setCityZip(cursor.getString(0));
                data.setCityName(cursor.getString(1));
                data.setCityState(cursor.getString(2));
                lv_myList.add(data);
            } while (cursor.moveToNext());
        }
        return lv_myList;
    }

    // Getting cities Count
    public int getCitiesCount() {
        SQLiteDatabase lv_db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(lv_db, TABLE_CITIES);
        return numRows;
    }
    // Updating single city
    // Probably will never use this.
    public int updateCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ZIP, city.getCityZip());
        values.put(KEY_NAME, city.getCityName());
        values.put(KEY_STATE, city.getCityState());

        return db.update(TABLE_CITIES, values, KEY_ZIP + " = ?",
                new String[] {city.getCityZip()}
        );
    }

    // Deleting single city
    public void deleteCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITIES, KEY_ZIP + " = ?",
                new String[] {city.getCityZip()}
        );
    }

    //delete all  cities & re-initialize database with defaults
    public void deleteAllAndReinit(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CITIES);
        //query above to delete all rows in table
        cf_initRows();
    }
}
