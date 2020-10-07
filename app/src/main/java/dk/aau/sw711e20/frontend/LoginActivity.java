package dk.aau.sw711e20.frontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.termux.R;
import com.termux.app.TermuxBridge;
import com.termux.app.TermuxService;

public class LoginActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

    }

    public void clickButton (View view) {

        if (isUserCorrect()) {
            Toast.makeText(getApplicationContext(), "Logged in!!", Toast.LENGTH_SHORT).show();
            goToJobActivity(view);
        } else
            Toast.makeText(getApplicationContext(), "not logged in :((", Toast.LENGTH_SHORT).show();
    }

    public boolean isUserCorrect () {
        EditText x = findViewById(R.id.editTextTextEmailAddress);
        String username = x.getText().toString();

        EditText y = findViewById(R.id.editTextTextPassword);
        String password = y.getText().toString();

        //TODO: connect user verification to GitHub?
        return username.equals("admin") && password.equals("admin");
    }

    public void goToJobActivity(View view) {
        Intent intent = new Intent(this, JobActivity.class);
        startActivity(intent);
    }
}
