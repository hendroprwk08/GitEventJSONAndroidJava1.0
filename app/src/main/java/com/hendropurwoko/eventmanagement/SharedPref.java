package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SharedPref {
    Context c;

    public SharedPref(Context c) {
        this.c = c;
    }

    public boolean cekSharedPreferences() {
        SharedPreferences mPrefs = c.getSharedPreferences("em",0);
        String str = mPrefs.getString("sp_email", "");

        if (str.length() != 0) return true;
        return false;
    }

    public void saveSharedPreferences(String email, String type, String username, String foto) {
        SharedPreferences mSharedPreferences = c.getSharedPreferences("em", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("sp_email", email);
        mEditor.putString("sp_type", type);
        mEditor.putString("sp_username", username);
        mEditor.putString("sp_foto", foto);
        mEditor.commit();
        mEditor.apply();
    }

    public void updateSharedPreferences(String key, String val){
        SharedPreferences mSharedPreferences = c.getSharedPreferences("em", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key, val);
        mEditor.commit();
    }

    void clearPreferences(){
        SharedPreferences mSharedPreferences = c.getSharedPreferences("em", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.clear().commit();
    }
}
