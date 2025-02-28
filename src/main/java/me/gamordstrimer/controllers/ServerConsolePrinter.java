package me.gamordstrimer.controllers;

import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ServerConsolePrinter {

    private static ServerConsolePrinter instance;

    private TextFlow server_console;
    private ScrollPane serverConsoleScrollPane;

    private ServerConsolePrinter() {
        // Private constructor to prevent instantiation
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

    public void printChatToConsole(String message, String stringColor, String[] styles) {
        Font font = javafx.scene.text.Font.getDefault();
        Text text = new Text(message);
        for (String style : styles) {
            switch (style) {
                case "bold":
                    font = javafx.scene.text.Font.font(font.getFamily(), javafx.scene.text.FontWeight.BOLD, font.getSize());
                    text.setFont(font);
                    break;
                case "italic":
                    font = javafx.scene.text.Font.font(font.getFamily(), javafx.scene.text.FontPosture.ITALIC, font.getSize());
                    text.setFont(font);
                    break;
                case "underlined":
                    text.setUnderline(true);
                    break;
                case "strikethrough":
                    text.setStrikethrough(true);
                    break;
            }
        }
        Color color = getColorFromString(stringColor);
        text.setFill(color);
    }

    private Color getColorFromString(String stringColor) {
        switch (stringColor) {
            case "black": return Color.BLACK;
            case "dark_blue": return new Color(0, 0, 170, 255);
            case "dark_green": return new Color(0, 170, 0, 255);
            case "dark_aqua": return new Color(0, 170, 170, 255);
            case "dark_red": return new Color(170, 0, 0,255);
            case "dark_purple": return new Color(170, 0, 170, 255);
            case "gold": return new Color(255, 170, 0, 255);
            case "gray": return Color.GRAY;
            case "dark_gray": return new Color(85, 85, 85, 255);
            case "blue": return new Color(85, 85, 255, 255);
            case "green": return new Color(85, 255, 85, 255);
            case "aqua": return new Color(85, 255, 255, 255);
            case "red": return new Color(255, 85, 85, 255);
            case "light_purple": return new Color(255, 85, 255, 255);
            case "yellow": return Color.YELLOW;
            case "white": return Color.WHITE;
            default: return Color.WHITE; // Default to white if unknown
        }
    }
}
