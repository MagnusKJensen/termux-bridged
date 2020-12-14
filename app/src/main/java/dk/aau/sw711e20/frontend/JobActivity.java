package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.termux.R;

import org.openapitools.client.apis.AssignmentApi;
import org.openapitools.client.models.DeviceId;
import org.openapitools.client.models.JobFiles;
import org.openapitools.client.models.Jobresult;
import org.openapitools.client.models.Result;
import org.openapitools.client.models.Statistics;
import org.openapitools.client.models.UserCredentials;

import dk.aau.sw711e20.ProcessingManager;
import dk.aau.sw711e20.TermuxHandler;

import static dk.aau.sw711e20.FileUtilsKt.decodeData;
import static dk.aau.sw711e20.FileUtilsKt.deleteJobFiles;
import static dk.aau.sw711e20.FileUtilsKt.encodeData;
import static dk.aau.sw711e20.FileUtilsKt.unzipJobToDisk;
import static dk.aau.sw711e20.FileUtilsKt.zipResult;
import static dk.aau.sw711e20.frontend.LoginActivity.SERVER_ADDRESS;

public class JobActivity extends Activity {

    private TextView jobStatus;
    private SharedPreferences.Editor editor;
    private SharedPreferences saved_values;

    private DeviceId deviceId;
    private UserCredentials userCredentials;

    private TermuxHandler termuxHandler;

    private JobFiles currentJob;

    private boolean isActivated = false;
    private boolean isSetupFinished = false;

    AssignmentApi assignmentApi;

    private TextView statusTextView;
    private Button activateButton;

    private ProcessingManager processingManager;
    private Thread jobHandlerThread;

    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.job_overview);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.savedPrefs(getApplicationContext());

        activateButton = findViewById(R.id.activateButton);
        statusTextView = findViewById(R.id.statusText);
        updateActivateButtonStatus();


        deleteJobFiles(this.getApplicationContext());
        termuxHandler = TermuxHandler.getInstance(this, this::onTermuxSetupFinished);
        assignmentApi = new AssignmentApi(SERVER_ADDRESS);
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        userCredentials = new UserCredentials(username, password);
        deviceId = new DeviceId(Preferences.getDeviceUUID(getApplicationContext()));



        processingManager = new ProcessingManager(this, userCredentials, statusTextView);
        jobHandlerThread = new Thread(processingManager);
        jobHandlerThread.start();
    }

    private void onTermuxSetupFinished() {
        this.isSetupFinished = true;
        updateActivateButtonStatus();
    }

    public void onSettingsButtonPressed(View view) {
        goToSettingsActivity(view);
    }

    public void goToSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onLogoutButtonPressed(View view) {
        editor.remove("login").commit();
        editor.remove("username").commit();
        goToLoginActivity(view);
    }

    public void onActivateButtonClicked(View view) {
        isActivated = !isActivated;
        updateActivateButtonStatus();
        if (isActivated) processingManager.activate();
        else processingManager.deactivate();
    }

    private void updateActivateButtonStatus(){
        activateButton.setTextColor(Color.WHITE);
        if (!isSetupFinished) {
            statusTextView.setText("Installing Python Interpreter");
            activateButton.setText("Activate");
            activateButton.setEnabled(false);
            return;
        } else {
            activateButton.setEnabled(true);
        }

        if (!isActivated) {
            activateButton.setText("Activate");
            statusTextView.setText("Deactivated");
            statusTextView.setTextColor(Color.parseColor("#a83636"));
            activateButton.setBackgroundColor(Color.parseColor("#329635"));
        } else {
            activateButton.setText("Deactivate");
            statusTextView.setText("Waiting for server...");
            statusTextView.setTextColor(Color.BLACK);
            activateButton.setBackgroundColor(Color.parseColor("#a83636"));
        }
    }



    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
