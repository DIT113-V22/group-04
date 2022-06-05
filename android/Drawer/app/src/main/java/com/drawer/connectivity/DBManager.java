package com.drawer.connectivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class serves purpose to mainly create and manage a SQLite database.
 * The methods are various queries to get values from the database.
 */
public class DBManager extends SQLiteOpenHelper {
    //Name of the database
    public static final String DB_NAME = "savedPaths";

    // below int is our database version
    private static final int DB_VERSION = 1;

    //variable for table name
    private static final String TABLE_NAME = "mySavedPath";
    private static final String ID_COL = "pathID";
    private static final String PATH_ANGLES_COL = "angleList";
    private static final String PATH_TITLE_COL = "savedName";
    private static final String PATH_SPEED_COL = "pathList";
    private static final String TIMER_VALUES_COL = "timerList";

    private static final String TABLE_NAME_POINTS = "mySavedPoints";
    private static final String POINTS_TITLE_COL = "savedNamePoints";
    private static final String POINTS_QUEUE_COL = "pointsQueue";

    /**
     * Constructor for DBManager.
     */
    public DBManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * This method creates new sqLiteDatabase object; creates a new table with columns.
     */
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

        String queryPoints = "CREATE TABLE " + TABLE_NAME_POINTS + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + POINTS_TITLE_COL + " TEXT,"
                + POINTS_QUEUE_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(queryPoints);
    }

    /**
     * This method is used to add new path, where a new path mean adding a new row to the table.
     * Every new path consists of all parameters.
     *
     * @param savedName name of recording
     * @param speedList list of speed instructions
     * @param angleList list of angle instructions
     * @param timerList list of timer commands
     */
    public void addNewPath(String savedName, String speedList, String angleList, String timerList) {
        // creating a variable for content values.
        ContentValues values = new ContentValues();

        // passing all the values along with its key and value pair.
        values.put(PATH_TITLE_COL, savedName);
        values.put(PATH_SPEED_COL, speedList);
        values.put(PATH_ANGLES_COL, angleList);
        values.put(TIMER_VALUES_COL, timerList);

        // creating a variable for sqlite database
        // and calling writable method to write data in our database
        SQLiteDatabase db = this.getWritableDatabase();

        // after adding all values we are passing content values to our table.
        db.insert(TABLE_NAME, null, values);

        // closing the database after adding database.
        db.close();
    }

    /**
     * This method is called to check if the table exists already.
     *
     * @param sqLiteDatabase the database we use
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POINTS);
        onCreate(sqLiteDatabase);
    }

    /**
     * This method runs a query which returns all the paths,
     * where each path stands for an entire row in the table.
     */
    @SuppressLint("Range")
    public ArrayList<String> getAllPaths() {
        ArrayList<String> arrayList = new ArrayList<>();
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


    /**
     * This method run a query with returns all the names of paths.
     *
     * @return arraylist of names of recordings
     */
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

    /**
     * This method runs a query which returns all the speed details of a specific path,
     * This methods takes in a path name to identify the details.
     *
     * @param pathName name of a recording
     *
     * @return array list of speed values
     */
    @SuppressLint("Range")
    public ArrayList<Integer> getSpeedDetails(String pathName) {
        ArrayList<Integer> speedArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select pathList from mySavedPath where savedName = '" + pathName + "' ", null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(PATH_SPEED_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("PathDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> speedArrayList.add(Integer.parseInt(item)));
        }

        return speedArrayList;
    }

    /**
     * This methods runs a query which returns the angles column value which matches with the given path name.
     *
     * @param pathName name of a recording
     *
     * @return array list of angle values
     */
    @SuppressLint("Range")
    public ArrayList<Integer> getAngleDetails(String pathName) {
        ArrayList<Integer> angleArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select angleList from mySavedPath where savedName = '" + pathName + "' ",
                null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(PATH_ANGLES_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("AngleDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> angleArrayList.add(Integer.parseInt(item)));
        }

        return angleArrayList;
    }

    /**
     * This methods runs a query which returns the time column value which matches with the given path name.
     *
     * @param pathName name of a recording
     *
     * @return array list of time values
     */
    @SuppressLint("Range")
    public ArrayList<Long> getTimeDetails(String pathName) {
        ArrayList<Long> timeArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select timerList from mySavedPath where savedName = '" + pathName + "' ",
                null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(TIMER_VALUES_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("TimeDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> timeArrayList.add(Long.parseLong(item)));
        }

        return timeArrayList;
    }

    /**
     * This method deletes a specific row from the database, by matching it with the given path name.
     *
     * @param pathName name of a recording
     */
    public void deleteSpecific(String pathName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from mySavedPath where savedName = '" + pathName + "' ");
        db.close();
    }
}
