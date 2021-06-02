package uk.ac.abertay.cmp309.WalkMapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WalkDB";
    private static final String TABLE_NAME = "Routes";
    private static final String[] COLUMN_NAMES = {"ID", "Name", "Rating", "Distance", "Coordinates", "Date"};
    private static SQLiteHelper instance = null;

    // create table string
    String creationString = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Rating REAL, Distance REAL, Coordinates BLOB, Date TEXT);";

    public SQLiteHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(creationString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // saves a new walk to database, creating a new row
    public void saveWalk(String name, float rating, double distance, List<LatLng> coordinates) {
        Log.d("WalkDebug", "saveWalk() received: " + name + " " + rating + " " + distance + " " + coordinates);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues row = new ContentValues();
        // prepare row for saving
        // get the current date
        Date currentDate = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);

        row.put(COLUMN_NAMES[1], name);
        row.put(COLUMN_NAMES[2], rating);
        row.put(COLUMN_NAMES[3], distance);
        row.put(COLUMN_NAMES[4], String.valueOf(coordinates));
        row.put(COLUMN_NAMES[5], formattedDate);
        db.insert(TABLE_NAME, null, row);
        db.close();
    }


    // this returns every record in the database
    public ArrayList<Walk> loadAllWalks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);
        ArrayList<Walk> list = new ArrayList<>();
        Walk walk = new Walk(0, null, 0, 0.0, null, null);
        while (result.moveToNext()) {
            walk = new Walk (
                result.getInt(result.getColumnIndex("ID")),
                result.getString(result.getColumnIndex("Name")),
                result.getFloat(result.getColumnIndex("Rating")),
                result.getDouble(result.getColumnIndex("Distance")),
                result.getString(result.getColumnIndex("Coordinates")),
                result.getString(result.getColumnIndex("Date"))
            );
            list.add(walk);
        }
        return list;
    }

    // return walks based on date
    // takes integer input which is how many days back to search
    public ArrayList<Walk> loadRecentWalks(int days) {
        SQLiteDatabase db = this.getReadableDatabase();
        String howManyDays = String.valueOf(days);
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE DATE > (SELECT DATE('now', '-" + howManyDays + " day')) ORDER BY date(DATE)";
        Cursor result = db.rawQuery(query, null);
        ArrayList<Walk> list = new ArrayList<>();
        Walk walk = new Walk(0, null, 0, 0.0, null, null);
        while (result.moveToNext()) {
            walk = new Walk (
                    result.getInt(result.getColumnIndex("ID")),
                    result.getString(result.getColumnIndex("Name")),
                    result.getFloat(result.getColumnIndex("Rating")),
                    result.getDouble(result.getColumnIndex("Distance")),
                    result.getString(result.getColumnIndex("Coordinates")),
                    result.getString(result.getColumnIndex("Date"))
            );
            Log.d("Date", "DATE: " + walk.getDate() + " --- DISTANCE: " + walk.getDistance());
            list.add(walk);
        }
        return list;
    }



}
