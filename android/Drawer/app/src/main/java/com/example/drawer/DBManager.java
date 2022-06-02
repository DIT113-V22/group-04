package com.example.drawer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

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


        String queryPoints = "CREATE TABLE " + TABLE_NAME_POINTS + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + POINTS_TITLE_COL + " TEXT,"
                + POINTS_QUEUE_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(queryPoints);
    }

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

    public void addNewPointsQueue(String savedNamePoints, String pointQueue) {

        // creating a variable for content values.
        ContentValues values = new ContentValues();

        // passing all the values along with its key and value pair.
        values.put(POINTS_TITLE_COL, savedNamePoints);
        values.put(POINTS_QUEUE_COL, pointQueue);


        // creating a variable for sqlite database
        // and calling writable method to write data in our database
        SQLiteDatabase db = this.getWritableDatabase();

        // after adding all values we are passing content values to our table.
        db.insert(TABLE_NAME_POINTS, null, values);

        // closing the database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // this method is called to check if the table exists already.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POINTS);
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
    public ArrayList<Integer> getAngleDetails(String pathName) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select angleList from mySavedPath where savedName = '" + pathName + "' ",
                null)) {
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
        try (Cursor res = db.rawQuery("select timerList from mySavedPath where savedName = '" + pathName + "' ",
                null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(TIMER_VALUES_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("TimeDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> arrayList.add(Long.parseLong(item)));
        }

        return arrayList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from mySavedPath");
        //db.execSQL("TRUNCATE table" + TABLE_NAME);
        db.close();
    }

    public void deleteSpecific(String pathName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from mySavedPath where savedName = '" + pathName + "' ");
        db.close();

    }

    @SuppressLint("Range")
    public ArrayList<String> getAllPoints() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery("select * from mySavedPoints", null)) {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                arrayList.add(res.getString(res.getColumnIndex(POINTS_TITLE_COL)));
                res.moveToNext();
            }
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllPointNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] str2 = new String[1];
        str2[0] = POINTS_TITLE_COL;
        try (Cursor res = db.query("mySavedPoints", str2, null, null, null, null, null)) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                arrayList.add(res.getString(res.getColumnIndex(POINTS_TITLE_COL)));
                res.moveToNext();
            }
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Integer> getPointDetails(String pointName) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor res = db.rawQuery(
                "select pointsQueue from mySavedPoints where savedNamePoints = '" + pointName + "' ",
                null)) {
            res.moveToFirst();
            String result = res.getString(res.getColumnIndex(POINTS_QUEUE_COL));
            result = result.replace(" ", "").replace("[", "").replace("]", "");
            System.out.println("PathDetails: " + result);
            Arrays.asList(result.split(",")).forEach(item -> arrayList.add(Integer.parseInt(item)));
        }
        return arrayList;
    }

    public void deleteAllPoints() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_POINTS,null,null);
        db.execSQL("delete from mySavedPoints");
        //db.execSQL("TRUNCATE table" + TABLE_NAME);
        db.close();
    }

    public String pointToString(Queue<Point> pointQueue) {
        StringBuilder stringBuilder = new StringBuilder();

        int pointQueueSize = pointQueue.size();

        for (int i = 0; i < pointQueueSize; i++) {

            Point tempPoint = pointQueue.poll();

            int pointX = 0;
            int pointY = 0;
            if (tempPoint != null) {
                pointX = tempPoint.x;
                pointY = tempPoint.y;
            }

                    stringBuilder.append(pointX);
            stringBuilder.append(",");
            stringBuilder.append(pointY);

            if (!(i == (pointQueueSize - 1))) {
                stringBuilder.append(":");
            }

        }

        return stringBuilder.toString();
    }

    public Queue<Point> stringToPoint(String pointQueueString) {
        Queue<Point> pointQueue = new LinkedList<>();

        String[] splitPoints = pointQueueString.split(":");

        for (String splitPoint : splitPoints) {
            int pointX;
            int pointY;

            String[] splitXY = splitPoint.split(",");

            pointX = Integer.parseInt(splitXY[0]);
            pointY = Integer.parseInt(splitXY[1]);

            pointQueue.add(new Point(pointX, pointY));
        }
        return pointQueue;
    }
}
