package com.idigital.asistenciasidigital;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private byte PRIVATE_MODE = 0;
    private static final String PREF_NAME = "IDigital";

    public PreferenceManager(Context context) {

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String value) {
        return pref.getString(key, value);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean value) {

        return pref.getBoolean(key, value);
    }

    public void putInt(String key, int value) {

        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int value) {

        return pref.getInt(key, value);
    }

    public void clearKeyPreference(String key) {
        editor.remove(key);
        editor.apply();
    }
}
