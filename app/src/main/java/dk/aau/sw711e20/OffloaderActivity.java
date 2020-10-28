package dk.aau.sw711e20;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.termux.R;
import com.termux.app.TermuxBridge;
import com.termux.app.TermuxService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OffloaderActivity extends Activity {

    TermuxBridge termuxBridge;
    private static final int STORAGE_PERMISSION_CODE = 1;

    String[] initializationCommands = {
        "apk update",
        "apk install clang",
        "apk install python",
        "ln -s /storage/emulated/0 storage",
        "termux-setup-storage"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        termuxBridge = new TermuxBridge(this);

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            setupTermuxService();
        }

        /*
        //termuxBridge.enqueueCommand("dpkg --configure -a", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("apt update", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("apt upgrade", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));

        termuxBridge.enqueueCommand("ln -s /storage/emulated/0 storage", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("termux-setup-storage", (output) -> System.out.println("Storage setup yielded: " + output + "\n"));

        termuxBridge.enqueueCommand("pwd", (output) -> System.out.println("PWD yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("ls -a", (output) -> System.out.println("ls -a yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("cd storage/Download", (output) -> System.out.println("cd yielded: " + output + "\n"));
        termuxBridge.enqueueCommand("ls -a", (output) -> System.out.println("ls -a yielded: " + output + "\n"));
        */
    }

    private void setupTermuxService(){
        Intent termuxServiceIntent = new Intent(this, TermuxService.class);
        startService(termuxServiceIntent);
        if (!bindService(termuxServiceIntent, termuxBridge, 0)) {
            throw new RuntimeException("Call to bindService() failed for termux service");
        }

        setContentView(R.layout.test_layout);
        TextView outputTextView = findViewById(R.id.output_text_view);
        termuxBridge.setOnTextChanged(outputTextView::setText);
        outputTextView.requestFocus();



        for (String command : initializationCommands)
            termuxBridge.enqueueCommand(command, (s) -> {});

        termuxBridge.enqueueCommand("cd storage", (s) -> {});
        termuxBridge.enqueueCommand("ls", (s) -> {});
        termuxBridge.enqueueCommand("pwd", (s) -> {});
        termuxBridge.enqueueCommand("cd Download", (s) -> {});
        termuxBridge.enqueueCommand("ls", (s) -> {});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            for (int i : grantResults) {
                System.out.println(i);
            }
            setupTermuxService();
        }
    }
}
