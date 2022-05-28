package com.example.drawer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DBManager extends SQLiteOpenHelper {
    //Name of the database
    public static final String DB_NAME = "savedPaths";

    // below int is our database version
    private static final int DB_VERSION = 1;

    //variable for table name
    private static final String TABLE_NAME = "mySavedPath";
    private static final String ID_COL = "pathID";
    private static final String PATH_ANGLES_COL = "angleList";
    private static String PATH_TITLE_COL = "savedName";
    private static final String PATH_SPEED_COL = "pathList";
    private static final String TIMER_VALUES_COL = "timerList";

    private final Type type = new TypeToken<ArrayList<String>>(){}.getType();

    public DBManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PATH_TITLE_COL + " TEXT,"
                + PATH_SPEED_COL + " TEXT,"
                + PATH_ANGLES_COL + " TEXT,"
                + TIMER_VALUES_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(query);
    }

    public void addNewPath(String savedName, String speedList, String angleList, String timerList) {

        // creating a variable for sqlite database
        // and calling writable method to write data in our database
        SQLiteDatabase db = this.getWritableDatabase();

        // creating a variable for content values.
        ContentValues values = new ContentValues();

        // passing all the values along with its key and value pair.
        values.put(PATH_TITLE_COL, savedName);
        values.put(PATH_SPEED_COL, speedList);
        values.put(PATH_ANGLES_COL, angleList);
        values.put(TIMER_VALUES_COL, timerList);

        // after adding all values we are passing content values to our table.
        db.insert(TABLE_NAME, null, values);

        // closing the database after adding database.
        db.close();
    }

    public void addNewTimer(String savedTimer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIMER_VALUES_COL, savedTimer);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // this method is called to check if the table exists already.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllPaths() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select * from mySavedPath", null)) {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                arrayList.add(res.getString(res.getColumnIndex(PATH_TITLE_COL)));
                res.moveToNext();
            }
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllPathNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] str2 = new String[1];
        str2[0] = PATH_TITLE_COL;
        try (Cursor res = db.query("mySavedPath", str2, null, null, null, null, null)) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                arrayList.add(res.getString(res.getColumnIndex(PATH_TITLE_COL)));
                res.moveToNext();
            }
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Integer> getPathDetails(String pathName) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select pathList from mySavedPath where savedName = '" + pathName + "' ", null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(PATH_SPEED_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("PathDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> arrayList.add(Integer.parseInt(item)));
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Integer> getAngleDetails(String pathName){
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select angleList from mySavedPath where savedName = '" + pathName + "' ", null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(PATH_ANGLES_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("AngleDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> arrayList.add(Integer.parseInt(item)));
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Long> getTimeDetails(String pathName) {
        ArrayList<Long> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select timerList from mySavedPath where savedName = '" + pathName + "' ", null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(TIMER_VALUES_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("TimeDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> arrayList.add(Long.parseLong(item)));
        }

        return arrayList;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from mySavedPath" );
        //db.execSQL("TRUNCATE table" + TABLE_NAME);
        db.close();
    }

    public void deleteSpecific(String pathName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from mySavedPath where savedName = '" + pathName + "' ");
        db.close();
    }
}
