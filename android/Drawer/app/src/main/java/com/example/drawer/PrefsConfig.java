package com.example.drawer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrefsConfig {
    private static final String LIST_KEY = "ListKey";

    public static void saveData(Context context, List list){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("saved paths list", json);
        editor.apply();
    }

    public static List loadData(Context context, List arrString){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("saved paths list", null);
        Type type = new TypeToken<ArrayList>(){}.getType();
        arrString = gson.fromJson(json, type);
        arrString = Arrays.asList(arrString);

        return arrString;

    }
}

