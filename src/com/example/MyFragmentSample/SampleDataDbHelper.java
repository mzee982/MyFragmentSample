package com.example.MyFragmentSample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SampleDataDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SampleData.db";
    public static final String QUERY_TYPE_MASTER_LIST = "QUERY_TYPE_MASTER_LIST";
    public static final String QUERY_TYPE_SLAVE = "QUERY_TYPE_SLAVE";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SampleDataContract.SampleData.TABLE_NAME + " (" +
                    SampleDataContract.SampleData._ID + " INTEGER PRIMARY KEY," +
                    SampleDataContract.SampleData.COLUMN_NAME_MASTER + TEXT_TYPE + COMMA_SEP +
                    SampleDataContract.SampleData.COLUMN_NAME_SLAVE + TEXT_TYPE +
                    ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SampleDataContract.SampleData.TABLE_NAME;

    private static SampleDataDbHelper _instance;

    private SampleDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SampleDataDbHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new SampleDataDbHelper(context);
        }

        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //
    public Cursor queryMasterList() {
        SQLiteDatabase db = getReadableDatabase();

        // Projection
        String[] projection = {
                SampleDataContract.SampleData._ID,
                SampleDataContract.SampleData.COLUMN_NAME_MASTER
        };

        // Sort
        String sortOrder = SampleDataContract.SampleData.COLUMN_NAME_MASTER;

        // Query
        return db.query(
                SampleDataContract.SampleData.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    //
    public Cursor querySlave(long id) {
        SQLiteDatabase db = getReadableDatabase();

        // Projection
        String[] projection = {
                SampleDataContract.SampleData._ID,
                SampleDataContract.SampleData.COLUMN_NAME_MASTER,
                SampleDataContract.SampleData.COLUMN_NAME_SLAVE
        };

        // Query
        return db.query(
                SampleDataContract.SampleData.TABLE_NAME,
                projection,
                SampleDataContract.SampleData._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    //
    public void insert(String master, String slave) {
        SQLiteDatabase db = getWritableDatabase();

        // Prepare
        ContentValues values = new ContentValues();
        values.put(SampleDataContract.SampleData.COLUMN_NAME_MASTER, master);
        values.put(SampleDataContract.SampleData.COLUMN_NAME_SLAVE, slave);

        // Insert
        db.insert(SampleDataContract.SampleData.TABLE_NAME, null, values);

    }

    //
    public void update(long id, String master, String slave) {
        SQLiteDatabase db = getWritableDatabase();

        // Prepare
        ContentValues values = new ContentValues();
        values.put(SampleDataContract.SampleData.COLUMN_NAME_MASTER, master);
        values.put(SampleDataContract.SampleData.COLUMN_NAME_SLAVE, slave);

        String selection = SampleDataContract.SampleData._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Update
        db.update(SampleDataContract.SampleData.TABLE_NAME, values, selection, selectionArgs);

    }

    //
    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();

        // Prepare
        String selection = SampleDataContract.SampleData._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // Delete
        db.delete(SampleDataContract.SampleData.TABLE_NAME, selection, selectionArgs);

    }

}
