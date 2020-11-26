package com.termux.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class TermuxBridge implements ServiceConnection {

    private static final int TERMINAL_WIDTH = 100, TERMINAL_HEIGHT = 200;
    private Consumer<String> onTextChanged;
    private Long currentJobStartTimeMillis = 0L;

    public void setOnTextChanged(Consumer<String> onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    public boolean isJobRunning(){
        return isExecutingCommand.get();
    }

    public long getCurrentCommandTime(){
        if (!isJobRunning()) return -1L;
        return System.currentTimeMillis() - currentJobStartTimeMillis;
    }

    private static class CommandBundle {
        private String command;
        private Consumer<String> onExecuted;

        public CommandBundle(String command, Consumer<String> outputConsumer) {
            this.command = command;
            this.onExecuted = outputConsumer;
        }
    }

    private final ConcurrentLinkedQueue<CommandBundle> commandQueue = new ConcurrentLinkedQueue<>();
    private CommandBundle currentCommand;
    private AtomicBoolean isExecutingCommand = new AtomicBoolean(true);
    private Activity activity;

    // Termux
    private TermuxService termService;
    private TerminalSession terminalSession;
    private TerminalEmulator terminalEmulator;

    public TermuxBridge(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        termService = ((TermuxService.LocalBinder) service).service;
        TermuxInstaller.setupIfNeeded(activity, () -> {});

        termService.mSessionChangeCallback = createTerminalChangeHandler();
        terminalSession = termService.createTermSession(null, null, null, false);
        terminalSession.initializeEmulator(100, 200);
        this.terminalEmulator = terminalSession.getEmulator();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        System.out.println("TERMUX SERVICE DISCONNECTED: " + componentName.flattenToString());
    }

    public void enqueueCommand(String command, Consumer<String> onFinished){
        commandQueue.add(new CommandBundle(command, onFinished));
        if (!isExecutingCommand.get()) startNextCommand();
    }

    private synchronized void parseOutput(String terminalOutput){
        String[] lines = terminalOutput.split("\n");
        String lastLine = lines[lines.length - 1].trim();
        if (lastLine.equals("Do you want to continue? [Y/n]") || lastLine.equals("Do you want to continue ? (y/n)")) {
            executeCommand("Y");
        } else if (lastLine.contains("[default=N]")) {
            executeCommand("N");
        } else if (lastLine.equals("$")) {
            // The terminal has finished processing the previous command
            if (currentCommand != null){
                currentCommand.onExecuted.accept(extractLatestOutput(terminalOutput));
                currentCommand = null;
            }
            startNextCommand();
        }
    }

    private void startNextCommand() {
        CommandBundle commandBundle = commandQueue.poll();
        if (commandBundle == null) {
            isExecutingCommand.set(false);
            System.out.println("No commands available for execution");
        } else {
            isExecutingCommand.set(true);
            System.out.println("Executing command: " + commandBundle.command);
            currentCommand = commandBundle;
            executeCommand(commandBundle.command);
        }
    }

    private void executeCommand(String command){
        terminalSession.write(command + "\n");
    }

    private String extractLatestOutput(String terminalOutput){
        // Split after last command
        String[] sections = terminalOutput.split(currentCommand.command);
        return sections[sections.length - 1].split("\\$")[0].trim(); // Exclude last line
    }

    private TerminalSession.SessionChangedCallback createTerminalChangeHandler() {
        return new TerminalSession.SessionChangedCallback() {
            @Override
            public void onTextChanged(TerminalSession changedSession) {
                String text = changedSession.getEmulator().
                    getSelectedText(0, 0, TERMINAL_WIDTH, TERMINAL_HEIGHT);
                parseOutput(text);

                if (onTextChanged != null) onTextChanged.accept(latestLines(text, 10));
            }

            @Override
            public void onTitleChanged(TerminalSession changedSession) { }

            @Override
            public void onSessionFinished(TerminalSession finishedSession) { }

            @Override
            public void onClipboardText(TerminalSession session, String text) { }

            @Override
            public void onBell(TerminalSession session) { }

            @Override
            public void onColorsChanged(TerminalSession session) { }
        };
    }

    private String latestLines(String fullText, int linesToInclude){
        String[] lines = fullText.split("\n");
        StringBuilder latestLines = new StringBuilder();
        for (int i = Math.max(0, lines.length - 1 - linesToInclude); i < lines.length; i++) {
            latestLines.append("\n").append(lines[i]);
        }
        return latestLines.toString();
    }

}
