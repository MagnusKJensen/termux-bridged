package com.termux.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;

public class TermuxBridge implements ServiceConnection {

    private Activity activity;

    private TermuxService termService;
    private TerminalSession terminalSession;
    private TerminalEmulator terminalEmulator;

    public TermuxBridge(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        termService = ((TermuxService.LocalBinder) service).service;
        TermuxInstaller.setupIfNeeded(activity, () -> {
        });

        terminalSession = termService.createTermSession(null, null, null, false);
        terminalSession.initializeEmulator(100, 100);
        this.terminalEmulator = terminalSession.getEmulator();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        // todo is a call to activity.finish() necessary here?
    }
}
