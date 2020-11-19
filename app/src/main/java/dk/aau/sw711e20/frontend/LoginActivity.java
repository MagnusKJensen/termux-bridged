package dk.aau.sw711e20.frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.termux.R;

import java.util.UUID;

public class LoginActivity extends Activity {

    TextView textEdit;
    SharedPreferences.Editor editor;
    SharedPreferences saved_values;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.saved_prefs(getApplicationContext());


        String s = saved_values.getString("login", "null");


        if (s.equals("true")) {
            String username = saved_values.getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        }


        //setContentView(R.layout.login_screen);

    }

    @SuppressLint("SetTextI18n")
    public void clickButton(View view) {
        textEdit = (TextView) findViewById(R.id.userDoesntExist);

        if (isUserCorrect()) {
            textEdit.setText("");
            String username = saved_values.getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        } else {
            textEdit.setText("User doesn't exist");
        }
    }

    public boolean isUserCorrect() {
        EditText x = findViewById(R.id.editTextTextEmailAddress);
        String username = x.getText().toString().replaceAll("\\s", "");
        EditText y = findViewById(R.id.editTextPassword);
        String password = y.getText().toString();

        //TODO: maybe set a limit in length?
        //TODO: Make authentication with username and password


        if (username.equals("Hannah") && password.equals("password")) {
            editor.putString("username", username).commit();
            editor.putString("password", password).commit();
            editor.putString("IMEI", getUUID()).commit();
            return true;
        }

        return false;
    }

    public void goToJobActivity() {
        Intent intent = new Intent(this, JobActivity.class);
        startActivity(intent);
    }

    public void goToNewUserActivity() {
        Intent intent = new Intent(this, CreateNewUserActivity.class);
        startActivity(intent);
    }

    public void pressCreateNewUser(View view) {
        editor.clear().commit();
        goToNewUserActivity();
    }

    public String getUUID() {
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        String uniqueID = saved_values.getString(PREF_UNIQUE_ID, "null");

        if (uniqueID.equals("null")) {
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, "null");
            if (uniqueID.equals("null")) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

}
