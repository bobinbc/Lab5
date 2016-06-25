package com.example.bobin_000.lab5;

/**
 * Created by bobin_000 on 6/25/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    //used for logging database version changes
    private static final String TAG = "DBAdapter";

    // Field Names:
    public static final String KEY_ROWID = "_id";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_IMAGE = "image";

    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_IMAGE};

    // DataBase info
    public static final String DATABASE_NAME = "dbContact";
    public static final String DATABASE_TABLE = "Contact";

    // The version number must be incremented each time a change to DB structure occurs.
    public static final int DATABASE_VERSION = 3;

    //SQL statement to create database
    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_FIRSTNAME + " TEXT NOT NULL, "
                    + KEY_LASTNAME + " TEXT, "
                    + KEY_IMAGE + " BLOB "
                    + ");";

    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to be inserted into the database.
    public boolean insertRow(String firstname, String lastname, byte[] image) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FIRSTNAME, firstname);
        initialValues.put(KEY_LASTNAME, lastname);
        initialValues.put(KEY_IMAGE, image);

        // Insert the data into the database.
        return db.insert(DATABASE_TABLE, null, initialValues) != 0;
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String firstname, String lastname, byte[] image) {
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_FIRSTNAME, firstname);
        newValues.put(KEY_LASTNAME, lastname);
        newValues.put(KEY_IMAGE, image);
        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}