package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Map;

public class SharePreferenceManager {


    public static void addToSharePreference(Task t, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(t.getTitle(),((t.isDone()) ? "T" : "F") + " " + t.getSubTitle());

        editor.apply();
    }

    public static ArrayList<Task> getFromSharePreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = sharedPreferences.getAll();

        ArrayList<Task> arrayList = new ArrayList<>();

        for (Map.Entry<String, ?> entry :
                map.entrySet()) {

            String key = entry.getValue().toString();
            char c = key.charAt(0);
            Task task = new Task(entry.getKey(), entry.getValue().toString().substring(2));
            task.setDone(c=='T');

            arrayList.add(task);
        }

        return arrayList;
    }

    public static void updateSharePreference(Task t ,Context context) {
        addToSharePreference(t, context);
    }
}
