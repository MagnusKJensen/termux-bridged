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

import androidx.annotation.Nullable;

import com.termux.R;

public class CreateNewUserActivity extends Activity {

    TextView textEdit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
    }

    @SuppressLint("SetTextI18n")
    public void createPress (View view) {
        if (isUserCorrect()) {
            String username = saved_prefs().getString("username", "null");
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();
            goToJobActivity();

        } else
            textEdit = (TextView) findViewById(R.id.userAlreadyExists);
            textEdit.setText("User already exists");
    }

    public boolean isUserCorrect () {
        EditText x = findViewById(R.id.createUsername);
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
        //intent.addFlags(Intent.FLAG_ACTIVITY_);
        startActivity(intent);
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
