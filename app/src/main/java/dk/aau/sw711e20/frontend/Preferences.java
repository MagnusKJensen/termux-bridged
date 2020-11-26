package dk.aau.sw711e20.frontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.openapitools.client.models.UserCredentials;

import java.util.Optional;
import java.util.UUID;

public class Preferences {

    private static final String USERNAME_TAG = "username";
    private static final String PASSWORD_TAG = "password";

    public static SharedPreferences.Editor prefEditor(Context appCon){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(appCon);
        SharedPreferences.Editor editor = saved_values.edit();
        return editor;
    }

    public static SharedPreferences savedPrefs(Context appCon){
        return PreferenceManager.getDefaultSharedPreferences(appCon);
    }

    public static Optional<UserCredentials> getSavedCredentials (Context appCon) {
        final String DEFAULT = "*default_value";
        String username = savedPrefs(appCon).getString("username", "*default_value");
        String password = savedPrefs(appCon).getString("password", "*default_value");

        if (username.equals(DEFAULT) || password.equals(DEFAULT))
            return Optional.empty();

        return Optional.of(new UserCredentials(username, password));
    }

    public static void saveLoginCredentials(Context appContext, UserCredentials userCredentials){
        final String DEFAULT = "*default_value";
        SharedPreferences.Editor editor = prefEditor(appContext);
        editor.putString(USERNAME_TAG, userCredentials.getUsername());
        editor.putString(PASSWORD_TAG, userCredentials.getPassword());
        editor.apply();
    }

    public static String getDeviceUUID(Context appContext) {
         SharedPreferences saved_values = (SharedPreferences) savedPrefs(appContext);

        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        String uniqueID = saved_values.getString(PREF_UNIQUE_ID, "null");

        if (uniqueID.equals("null")) {
            SharedPreferences sharedPrefs = appContext.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, "null");
            if (uniqueID.equals("null")) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

}
