package me.gamordstrimer.controllers;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServerConsolePrinter {

    private static ServerConsolePrinter instance;

    private TextFlow server_console;
    private ScrollPane serverConsoleScrollPane;

    private ConsolePrinter consolePrinter;

    private ServerConsolePrinter() {
        // Private constructor to prevent instantiation
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public static ServerConsolePrinter getInstance() {
        if (instance == null) {
            instance = new ServerConsolePrinter();
        }
        return instance;
    }

    public void setServerConsoleComponents(TextFlow server_console, ScrollPane serverConsoleScrollPane) {
        this.server_console = server_console;
        this.serverConsoleScrollPane = serverConsoleScrollPane;
    }

    public void printChatToConsole(JSONArray extra) {
        Platform.runLater(() -> {
            for (Object obj : extra) {
                if (obj instanceof  String) {
                    // If it's a raw string (like player name), create a default text object
                    Text textNode = new Text((String) obj);
                    textNode.setFill(Color.WHITE);
                    server_console.getChildren().add(textNode);
                } else if (obj instanceof JSONObject) {
                    JSONObject jsonText = (JSONObject) obj;
                    String text = jsonText.optString("text", ""); // Get text Content
                    String colorString = jsonText.optString("color", "white"); // Get Color
                    boolean isBold = jsonText.optBoolean("bold", false);
                    boolean isItalic = jsonText.optBoolean("italic", false);
                    boolean isUnderlined = jsonText.optBoolean("underlined", false);
                    boolean isStrikethrough = jsonText.optBoolean("strikethrough", false);
                    // boolean isObfuscated = jsonText.optBoolean("obfuscated", false);

                    Text styledText = new Text(text);
                    styledText.setFill(getColorFromString(colorString));

                    StringBuilder style = new StringBuilder();
                    if (isBold) style.append("-fx-font-weight: bold;");
                    if (isItalic) style.append("-fx-font-style: italic;");
                    if (isUnderlined) style.append("-fx-underline: true;");
                    if (isStrikethrough) style.append("-fx-strikethrough: true;");

                    styledText.setStyle(style.toString());
                    server_console.getChildren().add(styledText);
                } else {
                    consolePrinter.ErrorMessage("Unexpected JSON format: " + obj);
                }
            }

            server_console.getChildren().add(new Text("\n"));
        });
    }

    private Color getColorFromString(String stringColor) {
        switch (stringColor) {
            case "black": return Color.BLACK;
            case "dark_blue": return new Color(0 / 255.0, 0 / 255.0, 170 / 255.0, 1.0);
            case "dark_green": return new Color(0 / 255.0, 170 / 255.0, 0 / 255.0, 1.0);
            case "dark_aqua": return new Color(0 / 255.0, 170 / 255.0, 170 / 255.0, 1.0);
            case "dark_red": return new Color(170 / 255.0, 0 / 255.0, 0 / 255.0, 1.0);
            case "dark_purple": return new Color(170 / 255.0, 0 / 255.0, 170 / 255.0, 1.0);
            case "gold": return new Color(255 / 255.0, 170 / 255.0, 0 / 255.0, 1.0);
            case "gray": return Color.GRAY;
            case "dark_gray": return new Color(85 / 255.0, 85 / 255.0, 85 / 255.0, 1.0);
            case "blue": return new Color(85 / 255.0, 85 / 255.0, 255 / 255.0, 1.0);
            case "green": return new Color(85 / 255.0, 255 / 255.0, 85 / 255.0, 1.0);
            case "aqua": return new Color(85 / 255.0, 255 / 255.0, 255 / 255.0, 1.0);
            case "red": return new Color(255 / 255.0, 85 / 255.0, 85 / 255.0, 1.0);
            case "light_purple": return new Color(255 / 255.0, 85 / 255.0, 255 / 255.0, 1.0);
            case "yellow": return Color.YELLOW;
            case "white": return Color.WHITE;
            default: return Color.WHITE; // Default to white if unknown
        }
    }
}
