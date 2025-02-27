package me.gamordstrimer.app.controllers;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ConsolePrinter {

    private static ConsolePrinter instance;

    private TextFlow console;
    private ScrollPane consoleScrollPane;

    private ConsolePrinter() {
        // Private constructor to prevent instantiation
    }

    public static ConsolePrinter getInstance() {
        if (instance == null) {
            instance = new ConsolePrinter();
        }
        return instance;
    }

    public void setConsoleComponents(TextFlow console, ScrollPane consoleScrollPane) {
        this.console = console;
        this.consoleScrollPane = consoleScrollPane;
    }

    public void NormalMessage(String message) {
        message = "✔ " + message + "\n";
        printMessageToConsole(message, javafx.scene.paint.Color.GREEN);
    }

    public void WarningMessage(String message) {
        message = "⚠ " + message + "\n";
        printMessageToConsole(message, javafx.scene.paint.Color.ORANGE);
    }

    public void ErrorMessage(String message) {
        message = "✖ " + message + "\n";
        printMessageToConsole(message, javafx.scene.paint.Color.RED);
    }

    private void printMessageToConsole(String message, javafx.scene.paint.Color color) {
        if (console == null || consoleScrollPane == null) {
            System.err.println("ConsolePrinter UI components are not initialized!");
            return;
        }

        Platform.runLater(() -> {
            Text text = new Text(message);
            text.setFill(color);
            console.getChildren().add(text);
            consoleScrollPane.setVvalue(1.0);
        });
    }
}
