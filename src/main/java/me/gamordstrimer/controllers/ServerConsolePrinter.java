package me.gamordstrimer.controllers;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // ========================================================================
    // JSON TO CHAT CONSOLE
    // ========================================================================

    public void printJSONChatToConsole(JSONArray extra) {
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

    // ========================================================================
    // STRING TO CHAT CONSOLE
    // ========================================================================

    public void printSTRINGChatToConsole(String chatMessage) {
        Platform.runLater(() -> {
            // Regular expression to match the color code pattern (e.g., §a, §b, §l, etc.)
            Pattern pattern = Pattern.compile("§[0-9a-fA-Fk-oK-Or-sR-t-xXlmn]");  // Including formatting codes like §l, §o, etc.
            Matcher matcher = pattern.matcher(chatMessage);

            int lastMatchEnd = 0;  // Keep track of the last match's end index
            boolean isBold = false, isItalic = false, isUnderlined = false, isStrikethrough = false;  // Track text style
            Color currentColor = Color.WHITE;  // Default color

            while (matcher.find()) {
                // Extract the color/formatting code (e.g., "§a", "§b", §l, §o, etc.)
                String mcCode = matcher.group();

                // Extract the text between the color/formatting codes
                String textBeforeCode = chatMessage.substring(lastMatchEnd, matcher.start());
                Text plainText = new Text(textBeforeCode);  // Regular text before the color/formatting code
                plainText.setFill(currentColor);
                applyTextStyle(plainText, isBold, isItalic, isUnderlined, isStrikethrough);  // Apply styles
                server_console.getChildren().add(plainText);

                // Handle the color/formatting code
                if (mcCode.equals("§l")) {
                    isBold = true;  // Apply bold formatting
                } else if (mcCode.equals("§o")) {
                    isItalic = true;  // Apply italic formatting
                } else if (mcCode.equals("§n")) {
                    isUnderlined = true;  // Apply underline formatting
                } else if (mcCode.equals("§m")) {
                    isStrikethrough = true;  // Apply Strikethrough formatting
                } else if (mcCode.equals("§r")) {
                    isBold = isItalic = isUnderlined = isStrikethrough = false;  // Reset all styles
                    currentColor = Color.WHITE;  // Reset to default color
                } else {
                    // Handle the color codes (e.g., §a, §b, etc.)
                    currentColor = getColorFromMinecraftCode(mcCode);
                }

                lastMatchEnd = matcher.end();  // Update the last match's end index
            }

            // Add any remaining text after the last match
            String remainingText = chatMessage.substring(lastMatchEnd);
            if (!remainingText.isEmpty()) {
                Text remainingPlainText = new Text(remainingText);
                remainingPlainText.setFill(currentColor);
                applyTextStyle(remainingPlainText, isBold, isItalic, isUnderlined, isStrikethrough);  // Apply styles
                server_console.getChildren().add(remainingPlainText);
            }

            server_console.getChildren().add(new Text("\n"));
        });
    }

    private void applyTextStyle(Text textNode, boolean isBold, boolean isItalic, boolean isUnderlined, boolean isStrikethrough) {
        StringBuilder style = new StringBuilder();

        if (isBold) style.append("-fx-font-weight: bold;");
        if (isItalic) style.append("-fx-font-style: italic;");
        if (isUnderlined) style.append("-fx-underline: true;");
        if (isStrikethrough) style.append("-fx-strikethrough: true;");

        textNode.setStyle(style.toString());
    }

    private Color getColorFromMinecraftCode(String mcColorCode) {
        switch (mcColorCode) {
            case "§0": return Color.BLACK;
            case "§1": return new Color(0 / 255.0, 0 / 255.0, 170 / 255.0, 1.0);
            case "§2": return new Color(0 / 255.0, 170 / 255.0, 0 / 255.0, 1.0);
            case "§3": return new Color(0 / 255.0, 170 / 255.0, 170 / 255.0, 1.0);
            case "§4": return new Color(170 / 255.0, 0 / 255.0, 0 / 255.0, 1.0);
            case "§5": return new Color(170 / 255.0, 0 / 255.0, 170 / 255.0, 1.0);
            case "§6": return new Color(255 / 255.0, 170 / 255.0, 0 / 255.0, 1.0);
            case "§7": return Color.GRAY;
            case "§8": return new Color(85 / 255.0, 85 / 255.0, 85 / 255.0, 1.0);
            case "§9": return new Color(85 / 255.0, 85 / 255.0, 255 / 255.0, 1.0);
            case "§a": return new Color(85 / 255.0, 255 / 255.0, 85 / 255.0, 1.0);
            case "§b": return new Color(85 / 255.0, 255 / 255.0, 255 / 255.0, 1.0);
            case "§c": return new Color(255 / 255.0, 85 / 255.0, 85 / 255.0, 1.0);
            case "§d": return new Color(255 / 255.0, 85 / 255.0, 255 / 255.0, 1.0);
            case "§e": return Color.YELLOW;
            case "§f": return Color.WHITE;
            default: return Color.WHITE; // Default to white if unknown
        }
    }

    public void clearServerConsole() {
        if (server_console == null) {
            System.out.println("ServerConsole UI component is not initialized!");
            return;
        }
        Platform.runLater(() -> server_console.getChildren().clear());
    }
}
