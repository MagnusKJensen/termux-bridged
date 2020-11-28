package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.termux.R;

import org.openapitools.client.apis.AssignmentApi;
import org.openapitools.client.models.Assignment;
import org.openapitools.client.models.DeviceId;
import org.openapitools.client.models.UserCredentials;

import java.util.Arrays;

import dk.aau.sw711e20.TermuxHandler;

import static dk.aau.sw711e20.FileUtilsKt.decodeDownloadedData;
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

    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        termuxHandler = TermuxHandler.getInstance(this);
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        userCredentials = new UserCredentials(username, password);

        deviceId = new DeviceId(Preferences.getDeviceUUID(getApplicationContext()));

        setContentView(R.layout.job_overview);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.savedPrefs(getApplicationContext());
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

    public void onRequestJobButtonPressed(View view) {
        AssignmentApi assignmentApi = new AssignmentApi(SERVER_ADDRESS);
        Thread jobRequestThread = new Thread(() -> {
            try {
                Assignment assignment = assignmentApi.getJobForDevice(userCredentials, deviceId);
                unzipJobToDisk(getApplicationContext(), decodeDownloadedData(assignment.getFile()));
                termuxHandler.startExecutingPythonJob("main.py", (s) -> onJobFinishedExecuting());
            } catch (Exception e) {
                // todo  show in ui
                System.out.println("No job could be retrieved");
                e.printStackTrace();
            }
        });
        jobRequestThread.start();
    }

    private void onJobFinishedExecuting(){
        Log.i("JOB_FINISHED_", Arrays.toString(zipResult(getApplicationContext())));
    }

    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
