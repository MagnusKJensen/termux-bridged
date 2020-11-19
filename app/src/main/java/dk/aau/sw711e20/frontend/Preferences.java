package dk.aau.sw711e20.frontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static SharedPreferences.Editor prefEditor(Context appCon){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(appCon);
        SharedPreferences.Editor editor = saved_values.edit();
        return editor;
    }

    public static SharedPreferences saved_prefs(Context appCon){
        return PreferenceManager.getDefaultSharedPreferences(appCon);
    }

    public static UserCredentials getUserCredentials (Context appCon) {
        String username = saved_prefs(appCon).getString("username", "null");
        String password = saved_prefs(appCon).getString("password", "null");
        return new UserCredentials(username, password);
    }
}
