package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.termux.R;

import io.swagger.client.apis.UserApi;
import io.swagger.client.models.UserCredentials;

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
    public void onCreateUserPressed(View view) {
        UserCredentials enteredCredentials = getEnteredCredentials();
        boolean inputCorrect = verifyInput(enteredCredentials);

        if (!inputCorrect) {
            markErroneousInput("Invalid input");
        }

        Thread createUserRequestThread = new Thread(() -> {
            try {
                UserCredentials userCred = new UserApi("http://10.0.2.2:8080").createUser(enteredCredentials);
                goToJobActivity(userCred);
            } catch (Exception e) {
                Log.i("user_creation", e.getMessage());
                markErroneousInput("User with username " + enteredCredentials.getUsername() + " already exists");
            }
        });
        createUserRequestThread.start();
    }

    private void markErroneousInput(String errorMessage) {
        runOnUiThread(() -> {
                textEdit = (TextView) findViewById(R.id.userAlreadyExists);
                textEdit.setText(errorMessage);

                EditText usernameInputField = findViewById(R.id.createUsername);
                EditText passwordInputField = findViewById(R.id.editTextCreatePassword);
                // Todo: Hannah - make input text fields red if possible
            }
        );
    }

    private UserCredentials getEnteredCredentials() {
        EditText usernameInputField = findViewById(R.id.createUsername);
        String username = usernameInputField.getText().toString().replaceAll("\\s", "");
        EditText passwordInputField = findViewById(R.id.editTextCreatePassword);
        String password = passwordInputField.getText().toString();
        return new UserCredentials(username, password);
    }

    private boolean verifyInput(UserCredentials enteredCredentials) {
        // Password and username must have at least one character
        if (enteredCredentials.getUsername().isEmpty()) return false;
        if (enteredCredentials.getPassword().isEmpty()) return false;

        // Accepted
        return true;
    }

    public void goToJobActivity(UserCredentials userCredentials) {
        Intent intent = new Intent(this, JobActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", userCredentials.getUsername());
        bundle.putString("password", userCredentials.getPassword());
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
