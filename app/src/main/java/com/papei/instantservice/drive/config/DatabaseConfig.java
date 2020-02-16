package com.papei.instantservice.drive.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.papei.instantservice.drive.models.ViolationModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


// Database configuration
public class DatabaseConfig extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "speedometer_db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "create table " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME + "("
            + SpeedViolationContract.SpeedViolationEntry.ID + " integer primary key autoincrement,"
            + SpeedViolationContract.SpeedViolationEntry.LONGITUDE + " real,"
            + SpeedViolationContract.SpeedViolationEntry.LATITUDE + " real,"
            + SpeedViolationContract.SpeedViolationEntry.SPEED + " integer not null,"
            + SpeedViolationContract.SpeedViolationEntry.TIMESTAMP + " text);";

    private static final String DROP_TABLE = "drop table if exists " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME;

    private static final String SELECT_ALL = "select * from " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME + " order by timestamp desc";

    private static final String SELECT_ALL_BY_WEEK = "select * from " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME +
            " where timestamp > (select datetime('now', '-7 day')) order by timestamp desc";


    public DatabaseConfig(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Log.d("Database Operations", "Database is created...");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        Log.d("Database Operations", "Table is removed...");
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    // Add new record
    public void addViolation(ViolationModel s) {
        ContentValues values = new ContentValues();
        values.put(SpeedViolationContract.SpeedViolationEntry.LONGITUDE, s.getLongitude());
        values.put(SpeedViolationContract.SpeedViolationEntry.LATITUDE, s.getLatitude());
        values.put(SpeedViolationContract.SpeedViolationEntry.SPEED, s.getSpeed());
        values.put(SpeedViolationContract.SpeedViolationEntry.TIMESTAMP, s.getTimestamp().toString());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(SpeedViolationContract.SpeedViolationEntry.TABLE_NAME, null, values);
        System.out.println(values.toString());
        db.close();
    }

    // Get all records
    public List<ViolationModel> getViolations() {
        List<ViolationModel> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationModel violationModel = new ViolationModel();

            violationModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationModel.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationModel.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationModel.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            violationModel.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationModel);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

    // Get all records from last week
    public List<ViolationModel> getViolationsByWeek() {
        List<ViolationModel> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL_BY_WEEK, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationModel violationModel = new ViolationModel();

            violationModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationModel.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationModel.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationModel.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            violationModel.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationModel);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

    // Get all records based on timestamp
    public List<ViolationModel> getViolationsByTimestamp(String from, String to) {
        String SELECT_ALL_BY_TIMESTAMP = "select * from " + SpeedViolationContract.SpeedViolationEntry.TABLE_NAME +
                " where timestamp between '" + from + "' and '" + to + "' order by timestamp desc";

        List<ViolationModel> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL_BY_TIMESTAMP, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationModel violationModel = new ViolationModel();

            violationModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationModel.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationModel.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationModel.setSpeed(cursor.getInt(cursor.getColumnIndex("speed")));
            violationModel.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationModel);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

}
