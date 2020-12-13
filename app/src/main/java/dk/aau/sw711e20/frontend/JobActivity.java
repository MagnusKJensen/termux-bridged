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

    AssignmentApi assignmentApi;

    private TextView statusTextView;
    private Button activateButton;

    private Runnable jobManager;
    private Thread jobHandlerThread;

    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        termuxHandler = TermuxHandler.getInstance(this);
        assignmentApi = new AssignmentApi(SERVER_ADDRESS);
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        userCredentials = new UserCredentials(username, password);
        deviceId = new DeviceId(Preferences.getDeviceUUID(getApplicationContext()));

        setContentView(R.layout.job_overview);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.savedPrefs(getApplicationContext());

        activateButton = findViewById(R.id.activateButton);
        statusTextView = findViewById(R.id.statusText);
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

        /*if (!isActivated) {
            if (jobHandlerThread != null && jobHandlerThread.isAlive()) {
                jobManager
            }
        }*/


        /*
        Thread jobRequestThread = new Thread(() -> {
            try {
                currentJob = assignmentApi.getJobForDevice(userCredentials, deviceId);
                unzipJobToDisk(getApplicationContext(), decodeData(currentJob.getData()));
                termuxHandler.startExecutingPythonJob("main.py", (s) -> postJobResult());
            } catch (Exception e) {
                // todo  show in ui
                System.out.println("No job could be retrieved");
                e.printStackTrace();
            }
        });
        jobRequestThread.start();*/
    }

    private void updateActivateButtonStatus(){
        activateButton.setTextColor(Color.WHITE);
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

    private void postJobResult() {
        Thread postResultThread = new Thread(() -> {
            try {
                byte[] resultData = encodeData(zipResult(getApplicationContext()));
                //Result result = new Result(currentJob.getJobid(), resultData, new Statistics(true, 0L)); // todo Cpu time? Client side vs server side?
                Jobresult jobresult = new Jobresult(new JobFiles(currentJob.getJobid(), resultData));
                assignmentApi.uploadJobResult(userCredentials, deviceId, currentJob.getJobid(), jobresult);
                deleteJobFiles(getApplicationContext());
                currentJob = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        postResultThread.start();
    }

    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
