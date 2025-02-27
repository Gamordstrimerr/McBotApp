package me.gamordstrimer.network.packets.play.clientbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gamordstrimer.app.ui.McBotAppUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatMessagePacket02 {

    public void processIncomingMessages(String chatMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(chatMessage);
            JsonNode extraNode = rootNode.path("extra");

            if (extraNode.isArray() && extraNode.size() > 0) {
                McBotAppUI appUI = McBotAppUI.getInstance();
                if (appUI != null) {
                    JTextPane serverConsole = appUI.getServer_console();
                    if (serverConsole != null) {
                        JsonNode firstNode = extraNode.get(0);

                        if (firstNode.isObject()) { // Case 1: JSON object with color and text
                            String message = firstNode.path("text").asText();
                            String color = firstNode.path("color").asText();
                            appendColoredText(serverConsole, message + "\n", getColorFromString(color));
                        } else if (firstNode.isTextual()) { // Case 2: Plain text string
                            appendColoredText(serverConsole, firstNode.asText() + "\n", Color.WHITE);
                        }
                    } else {
                        System.out.println("Server_console is Null.");
                    }
                } else {
                    System.out.println("Instance AppUI is Null.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void appendColoredText(JTextPane textPane, String text, Color color) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private Color getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "black": return Color.BLACK;
            case "dark_blue": return new Color(0, 0, 170);
            case "dark_green": return new Color(0, 170, 0);
            case "dark_aqua": return new Color(0, 170, 170);
            case "dark_red": return new Color(170, 0, 0);
            case "dark_purple": return new Color(170, 0, 170);
            case "gold": return new Color(255, 170, 0);
            case "gray": return Color.GRAY;
            case "dark_gray": return new Color(85, 85, 85);
            case "blue": return new Color(85, 85, 255);
            case "green": return new Color(85, 255, 85);
            case "aqua": return new Color(85, 255, 255);
            case "red": return new Color(255, 85, 85);
            case "light_purple": return new Color(255, 85, 255);
            case "yellow": return Color.YELLOW;
            case "white": return Color.WHITE;
            default: return Color.WHITE; // Default to white if unknown
        }
    }


    // Method for debug Message Reception
    public void debugChatMessage(String chatMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the JSON string to a readable (pretty) format
            Object json = objectMapper.readValue(chatMessage, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println("[CHAT_MESSAGE] " + prettyJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
