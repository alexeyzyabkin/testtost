package com.invizorys.mobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.invizorys.mobile.model.User;

/**
 * Created by Paryshkura Roman on 13.12.2015.
 */
public class Settings {

    public static final String USER = "user";
    public static final String APP_NAME = "Matinee";
    public static final String EVENT_ID = "eventId";

    public static void saveUser(Activity activity, User user) {
        SharedPreferences mPrefs = activity.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString(USER, json);
        prefsEditor.apply();
    }

    public static User fetchUser(Activity activity) {
        Gson gson = new Gson();
        SharedPreferences mPrefs = activity.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        String json = mPrefs.getString(USER, "");
        return gson.fromJson(json, User.class);
    }

    public static void saveCurrentEventId(Activity activity, Long currentEventId) {
        SharedPreferences mPrefs = activity.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putLong(EVENT_ID, currentEventId);
        prefsEditor.apply();
    }

    public static Long fetchCurrentEventId(Activity activity) {
        SharedPreferences mPrefs = activity.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return mPrefs.getLong(EVENT_ID, -1);
    }

}