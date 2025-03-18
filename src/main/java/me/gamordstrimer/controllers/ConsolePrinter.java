package me.gamordstrimer.controllers;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import me.gamordstrimer.utils.LogsManager;

public class ConsolePrinter {

    private static ConsolePrinter instance;
    private LogsManager logsManager;

    private TextFlow console;
    private ScrollPane consoleScrollPane;

    private ConsolePrinter() {
        // Private constructor to prevent instantiation
        this.logsManager = LogsManager.getInstance();
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
        printMessageToConsole(message, javafx.scene.paint.Color.GREEN, false);
    }

    public void WarningMessage(String message) {
        message = "🔔 " + message + "\n";
        printMessageToConsole(message, javafx.scene.paint.Color.ORANGE, true);
    }

    public void ErrorMessage(String message) {
        message = "❌ " + message + "\n";
        printMessageToConsole(message, javafx.scene.paint.Color.RED, true);
    }

    private void printMessageToConsole(String message, javafx.scene.paint.Color color, boolean bold) {
        if (console == null || consoleScrollPane == null) {
            System.err.println("ConsolePrinter UI components are not initialized!");
            return;
        }

        logsManager.writeInLogFile(message);

        Platform.runLater(() -> {
            Text text = new Text(message);
            text.setFill(color);

            // Set a font that supports emojis (e.g., Segoe UI Emoji, Noto Color Emoji, or Apple Color Emoji)
            text.setStyle("-fx-font-family: 'Segoe UI Emoji';");

            // Make the text bold if the bold flag is true
            if (bold) {
                text.setStyle("-fx-font-weight: bold;");
            }

            console.getChildren().add(text);
            // consoleScrollPane.setVvalue(1.0);
        });
    }

    public void clearConsole() {
        if (console == null) {
            System.out.println("ConsolePrinter UI component is not initialized!");
            return;
        }
        Platform.runLater(() -> console.getChildren().clear());
    }
}
