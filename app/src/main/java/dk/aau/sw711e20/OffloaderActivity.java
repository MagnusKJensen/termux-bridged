package dk.aau.sw711e20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.termux.app.TermuxBridge;
import com.termux.app.TermuxService;

public class OffloaderActivity extends Activity {

    TermuxBridge termuxBridge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        termuxBridge = new TermuxBridge(this);

        Intent termuxServiceIntent = new Intent(this, TermuxService.class);
        startService(termuxServiceIntent);
        if (!bindService(termuxServiceIntent, termuxBridge, 0)) {
            throw new RuntimeException("Call to bindService() failed for termux service");
        }


        //termuxBridge.enqueueCommand("dpkg --configure -a", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("apt update", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("apt upgrade", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));

        termuxBridge.enqueueCommand("termux-setup-storage", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));

        termuxBridge.enqueueCommand("pwd", (output) -> System.out.println("PWD yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("cd storage/downloads/", (output) -> System.out.println("cd yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("pwd", (output) -> System.out.println("PWD yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("ls -a", (output) -> System.out.println("ls -a yielded: " + output + "\n"));
    }


}
