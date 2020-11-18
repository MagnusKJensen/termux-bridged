package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.R;

public class LoginActivity extends Activity {

    TextView textEdit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String s = saved_prefs().getString("login", "null");

        if (s.equals("true")){
            String username = saved_prefs().getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        }

        setContentView(R.layout.login_screen);

    }

    @SuppressLint("SetTextI18n")
    public void clickButton (View view) {

        if (isUserCorrect()) {
            String username = saved_prefs().getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        } else
            textEdit = (TextView) findViewById(R.id.userDoesntExist);
            textEdit.setText("User doesn't exist");
    }

    public boolean isUserCorrect () {
        EditText x = findViewById(R.id.editTextTextEmailAddress);
        String username = x.getText().toString().replaceAll("\\s","");

        //TODO: maybe set a limit in length?


        if(username.equals("Hannah")){
            prefEditor().putString("username", username);
            prefEditor().commit();
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

    public void pressCreateNewUser (View view) {
        prefEditor().clear().commit();
        goToNewUserActivity();
    }

    public SharedPreferences.Editor prefEditor(){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = saved_values.edit();
        return editor;
    }

    public SharedPreferences saved_prefs(){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return saved_values;
    }
}
