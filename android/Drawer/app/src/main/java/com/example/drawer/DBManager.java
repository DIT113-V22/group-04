package com.example.drawer;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {
    //Name of the database
    public static final String DB_NAME = "savedPaths";

    // below int is our database version
    private static final int DB_VERSION = 1;

    //variable for table name
    private static final String TABLE_NAME = "mySavedPath";

    private static final String ID_COL = "pathID";
    private static final String PATH_TITLE_COL = "savedName";
    private static final String PATH_VALUES_COL = "pathList";

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
                + PATH_VALUES_COL + " TEXT) ";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(query);

    }

    public void addNewPath(String savedName, String pathList) {

        // creating a variable for sqlite database
        // and calling writable method to write data in our database
        SQLiteDatabase db = this.getWritableDatabase();

        // creating a variable for content values.
        ContentValues values = new ContentValues();

        // passing all the values along with its key and value pair.
        values.put(PATH_TITLE_COL, savedName);
        values.put(PATH_VALUES_COL, pathList);

        // after adding all values we are passing content values to our table.
        db.insert(TABLE_NAME, null, values);

        // closing the database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // this method is called to check if the table exists already.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
    @SuppressLint("Range")
    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mySavedPath", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(PATH_TITLE_COL)));
            res.moveToNext();
        }
        return array_list;
    }

}
