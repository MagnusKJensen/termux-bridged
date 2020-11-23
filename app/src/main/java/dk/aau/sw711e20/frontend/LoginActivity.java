package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.R;

import java.util.Optional;

import io.swagger.client.apis.UserApi;
import io.swagger.client.models.DeviceId;
import io.swagger.client.models.UserCredentials;

public class LoginActivity extends Activity {

    TextView textEdit;
    SharedPreferences.Editor editor;
    SharedPreferences saved_values;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_screen);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.saved_prefs(getApplicationContext());

        Optional<UserCredentials> savedCredentials = Preferences.getSavedCredentials(getApplicationContext());

        if (savedCredentials.isPresent()) {
            UserCredentials cred = savedCredentials.get();
            setUserNameText(cred.getUsername());
            setPasswordText(cred.getPassword());
            attemptLogin(cred);
        }
        //setContentView(R.layout.login_screen);
    }

    @SuppressLint("SetTextI18n")
    public void onLoginButtonClicked(View view) {
        UserCredentials userCredentials = getEnteredCredentials();

        if (!isInputCorrectFormat(userCredentials)) {
            textEdit = (TextView) findViewById(R.id.userDoesntExist);
            textEdit.setText("Invalid username or password");
        }

        Thread loginThread = new Thread(() -> attemptLogin(userCredentials));
        loginThread.start();
    }

    private void attemptLogin(UserCredentials userCredentials) {
        try {
            UserCredentials userCred = new UserApi("http://10.0.2.2:8080").login(userCredentials, new DeviceId(saved_values.getString("PREF_UNIQUE_ID", "null")));
            onLoginSuccess(userCred);
        } catch (Exception e) {
            Log.i("user_.login", e.getMessage());
            onLoginFailed(userCredentials);
        }
    }

    private void onLoginFailed(UserCredentials userCredentials) {
        textEdit = (TextView) findViewById(R.id.userDoesntExist);
        runOnUiThread( () -> textEdit.setText("Invalid username or password"));
    }

    private void onLoginSuccess(UserCredentials userCredentials) {
        goToJobActivity(userCredentials);
    }

    private UserCredentials getEnteredCredentials(){
        EditText x = findViewById(R.id.editTextTextEmailAddress);
        String username = x.getText().toString().replaceAll("\\s", "");
        EditText y = findViewById(R.id.editTextPassword);
        String password = y.getText().toString();
        return new UserCredentials(username, password);
    }

    public boolean isInputCorrectFormat(UserCredentials userCredentials) {
        return !userCredentials.getUsername().isEmpty() && !userCredentials.getPassword().isEmpty();
    }

    public void setUserNameText(String userNameText) {
        EditText userNameEditText = findViewById(R.id.editTextTextEmailAddress);
        userNameEditText.setText(userNameText);
    }

    public void setPasswordText(String passwordText) {
        EditText passwordEdit = findViewById(R.id.editTextPassword);
        passwordEdit.setText(passwordText);
    }

    public void goToJobActivity(UserCredentials userCredentials) {
        Intent intent = new Intent(this, JobActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", userCredentials.getUsername());
        bundle.putString("password", userCredentials.getPassword());
        intent.putExtras(bundle);
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

    /*
    public SharedPreferences.Editor prefEditor(){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = saved_values.edit();
        return editor;
    }

    public SharedPreferences saved_prefs(){
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return saved_values;
    }*/
}
