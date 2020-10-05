package com.termux.app;

public class CommandException extends Exception{

    private String terminalOutput;

    public CommandException(String message) {
        super(message);
    }
}
