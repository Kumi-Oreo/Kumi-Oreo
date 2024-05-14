package com.example.research;

import android.content.Context;
import android.content.SharedPreferences;

public class loginSave {


    public static final String SHARED_PREF_NAME = "MySharedPref";
    public static final String KEY_NUMBER = "number";

    private final SharedPreferences sharedPreferences;

    public loginSave(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveNumber(int number) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NUMBER, number);
        editor.apply();
    }
    public int getSavedNumber() {
        return sharedPreferences.getInt(KEY_NUMBER, 0); // 0 is the default value if the key doesn't exist
    }

}
