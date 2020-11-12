package dk.aau.sw711e20.frontend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.termux.R;

public class LoginActivity extends Activity {

    SharedPreferences.Editor editor;
    SharedPreferences saved_values;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = saved_values.edit();

        String s = saved_values.getString("login", "null");

        if (s.equals("true")){
            String username = saved_values.getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        }

        setContentView(R.layout.login_screen);

    }

    public void clickButton (View view) {

        if (isUserCorrect()) {
            String username = saved_values.getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();
        } else
            Toast.makeText(getApplicationContext(), "not logged in :((", Toast.LENGTH_SHORT).show();
    }

    public boolean isUserCorrect () {
        EditText x = findViewById(R.id.editTextTextEmailAddress);
        String username = x.getText().toString().replaceAll("\\s","");

        //TODO: maybe set a limit in length?
        if(!username.equals("")){
            editor.putString("username", username);
            editor.commit();
            return true;
        }

        return false;
    }

    public void goToJobActivity() {
        Intent intent = new Intent(this, JobActivity.class);
        startActivity(intent);
    }
}
