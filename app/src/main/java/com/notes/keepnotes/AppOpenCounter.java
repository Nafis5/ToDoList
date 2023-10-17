package com.notes.keepnotes;

import android.content.Context;
import android.content.SharedPreferences;

public class AppOpenCounter {
    private static final String PREF_NAME = "AppOpenCounterPrefs";
    private static final String KEY_APP_OPEN_COUNT = "appOpenCount";
    private static final String KEY_APP_SUBSCRIPTION_COUNT = "appOpenCount";

    private Context context;

    public AppOpenCounter(Context context) {
        this.context = context;

    }

    public void incrementAppOpen() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentCount = prefs.getInt(KEY_APP_OPEN_COUNT, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_APP_OPEN_COUNT, currentCount + 1);
        editor.apply();
    }
    public void incrementSubscriptionOpen() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentCount = prefs.getInt(KEY_APP_SUBSCRIPTION_COUNT, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_APP_SUBSCRIPTION_COUNT, currentCount + 1);
        editor.apply();
    }


    public int getAppOpenNumber() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_APP_OPEN_COUNT, 0);
    }
    public int getSubscriptionOpenNumber() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_APP_SUBSCRIPTION_COUNT, 0);
    }
}

