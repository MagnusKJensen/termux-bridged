package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.termux.R;

public class CreateNewUserActivity extends Activity {

    TextView textEdit;
    SharedPreferences.Editor editor;
    SharedPreferences saved_values;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.saved_prefs(getApplicationContext());
    }

    @SuppressLint("SetTextI18n")
    public void createPress (View view) {
        textEdit = (TextView) findViewById(R.id.userAlreadyExists);
        if (isUserCorrect()) {
            textEdit.setText("");
            String username = saved_values.getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();

        } else {
            textEdit.setText("User already exists");
        }
    }

    public boolean isUserCorrect () {
        EditText x = findViewById(R.id.createUsername);
        String username = x.getText().toString().replaceAll("\\s","");
        EditText y = findViewById(R.id.editTextCreatePassword);
        String password = y.getText().toString();

        //TODO: maybe set a limit in length?
        //TODO: euthentication check if username/user already exists


        if(username.equals("Hannah") && password.equals("password")){
            editor.putString("username", username).commit();
            editor.putString("password", password).commit();
            return true;
        }

        return false;
    }

    public void goToJobActivity() {
        Intent intent = new Intent(this, JobActivity.class);
        startActivity(intent);
    }

}
