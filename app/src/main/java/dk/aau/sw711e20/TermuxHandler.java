package dk.aau.sw711e20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.termux.R;
import com.termux.app.TermuxBridge;
import com.termux.app.TermuxService;

import java.util.function.Consumer;

public class TermuxHandler {

    private static final String[] initializationCommands = {
        "pkg update",
        "dpkg --configure -a",
        "pkg install clang",
        "pkg install python",
        "ln -s /storage/emulated/0 storage",
        "termux-setup-storage",
        "cd ..",
        "mkdir " + FileUtilsKt.mainFolderName,
        "cd " + FileUtilsKt.mainFolderName,
        "mkdir " + FileUtilsKt.jobFilesFolderName,
        "pwd",
        "ls -a"
    };

    private final TermuxBridge termuxBridge;

    private TermuxHandler(Activity activity) {
        termuxBridge = new TermuxBridge(activity);
        setupTermuxService(activity);
    }

    private static TermuxHandler instance;
    public static TermuxHandler getInstance(Activity activity) {
        if (instance == null) instance = new TermuxHandler(activity);
        return instance;
    }

    private void setupTermuxService(Context context){
        Intent termuxServiceIntent = new Intent(context, TermuxService.class);
        context.startService(termuxServiceIntent);
        if (!context.bindService(termuxServiceIntent, termuxBridge, 0)) {
            throw new RuntimeException("Call to bindService() failed for termux service");
        }

        for (String command : initializationCommands) {
            termuxBridge.enqueueCommand(command, (s) -> {
                Log.i("TermuxCommand", "Executed: " + command + "\nGot answer : " + s);
            });
        }
    }

    public void startExecutingPythonJob(String mainFileName, Consumer<String> onFinished){
        termuxBridge.enqueueCommand("mkdir " + FileUtilsKt.jobFilesFolderName, (s) -> {});
        termuxBridge.enqueueCommand("cd " + FileUtilsKt.jobFilesFolderName, (s) -> {});
        termuxBridge.enqueueCommand("mkdir " + FileUtilsKt.resultsFolderName, (s) -> {});
        termuxBridge.enqueueCommand("python " + mainFileName, (s) -> {});
        termuxBridge.enqueueCommand("cd .. ", onFinished);

    }

    private void deleteJobFiles(){

    }




}
