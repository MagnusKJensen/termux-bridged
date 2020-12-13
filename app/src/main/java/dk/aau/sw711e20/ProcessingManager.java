package dk.aau.sw711e20;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.openapitools.client.apis.AssignmentApi;
import org.openapitools.client.models.DeviceId;
import org.openapitools.client.models.JobFiles;
import org.openapitools.client.models.Jobresult;
import org.openapitools.client.models.UserCredentials;

import dk.aau.sw711e20.frontend.Preferences;

import static dk.aau.sw711e20.FileUtilsKt.decodeData;
import static dk.aau.sw711e20.FileUtilsKt.deleteJobFiles;
import static dk.aau.sw711e20.FileUtilsKt.encodeData;
import static dk.aau.sw711e20.FileUtilsKt.unzipJobToDisk;
import static dk.aau.sw711e20.FileUtilsKt.zipResult;
import static dk.aau.sw711e20.frontend.LoginActivity.SERVER_ADDRESS;

public class ProcessingManager implements Runnable {

    private enum Status {
        NO_JOB, WAITING, PROCESSING, DONE, UPLOADING;
    }

    private boolean activated = false;

    private TermuxHandler termuxHandler;
    private AssignmentApi assignmentApi;
    private UserCredentials userCredentials;

    private Status currentJobStatus = Status.NO_JOB;
    private boolean hasJob = false;
    private JobFiles currentJob;

    private Activity activity;
    private DeviceId deviceId;

    private TextView statusTextView;

    public ProcessingManager(Activity activity, UserCredentials userCredentials, TextView textView) {
        this.statusTextView = textView;
        this.activity = activity;
        termuxHandler = TermuxHandler.getInstance(activity);
        assignmentApi = new AssignmentApi(SERVER_ADDRESS);
        this.userCredentials = userCredentials;
        deviceId = new DeviceId(Preferences.getDeviceUUID(activity.getApplicationContext()));
    }

    public void deactivate() {
        //activated = false; todo REIMPLEMENT
    }

    public void activate() {
        activated = true;
    }

    @Override
    public void run() {
        while (true) {
            if (activated) {
                try {
                    nextStep();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void nextStep() throws InterruptedException {
        if (isCurrentStatus(Status.NO_JOB)) {
            currentJob = fetchJob();
            if (currentJob == null) {
                Thread.sleep(2000);
                return;
            } else {
                unzipJobToDisk(activity.getApplicationContext(), decodeData(currentJob.getData()));
                setJobStatus(Status.WAITING);
            }
        }

        if (isCurrentStatus(Status.WAITING)) {
            setJobStatus(Status.PROCESSING);
            termuxHandler.startExecutingPythonJob("main.py", this::onJobFinished);
        }

        if (isCurrentStatus(Status.DONE)) {
            postJobResult();
            deleteJobFiles(activity.getApplicationContext());
            setJobStatus(Status.NO_JOB);
        }
    }

    private JobFiles fetchJob() {
        try {
            return assignmentApi.getJobForDevice(userCredentials, deviceId);
        } catch (Exception e) {
            Log.i("ProcessingManager:nextStep()", "Could not get job");
            return null;
        }
    }

    private void onJobFinished(String output) {
        setJobStatus(Status.DONE);
    }

    private synchronized void setJobStatus(Status newStatus) {
        this.currentJobStatus = newStatus;
        activity.runOnUiThread(() -> statusTextView.setText(newStatus.name()));
    }

    private synchronized boolean isCurrentStatus(Status status) {
        return this.currentJobStatus == status;
    }


    private void postJobResult() {
        try {
            byte[] resultData = encodeData(zipResult(activity.getApplicationContext()));
            Jobresult jobresult = new Jobresult(new JobFiles(currentJob.getJobid(), resultData));
            assignmentApi.uploadJobResult(userCredentials, deviceId, currentJob.getJobid(), jobresult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
