package me.gamordstrimer.controllers;

public class ServerConsolePrinter {

    private static ServerConsolePrinter instance;

    private ServerConsolePrinter() {
        // Private constructor to prevent instantiation
    }

    public static ServerConsolePrinter getInstance() {
        if (instance == null) {
            instance = new ServerConsolePrinter();
        }
        return instance;
    }
}
