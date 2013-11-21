package com.doomonafireball.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: derek Date: 5/10/12 Time: 7:32 PM
 */
public class SharedPrefsUtil {

    private SharedPreferences dataPrefs;
    private SharedPreferences.Editor dataEditor;

    private static final String DATA_PREFS_TAG = "PhraseShifterData";
    private static final String PHRASES_AS_JSON = "PhrasesAsJson";

    public static SharedPrefsUtil instance;

    public static void initialize(Context c) {
        if (instance == null) {
            instance = new SharedPrefsUtil(c);
        }
    }


    public static SharedPrefsUtil getInstance() {
        return instance;
    }

    private SharedPrefsUtil(Context c) {
        dataPrefs = c.getSharedPreferences(DATA_PREFS_TAG, 0);
        dataEditor = dataPrefs.edit();
    }

    public String getPhrasesAsJson() {
        return dataPrefs.getString(PHRASES_AS_JSON, "");
    }

    public void setPhrasesAsJson(String phrases) {
        dataEditor.putString(PHRASES_AS_JSON, phrases);
        dataEditor.commit();
    }
}
