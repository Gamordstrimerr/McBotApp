package me.gamordstrimer.app.controllers;

import me.gamordstrimer.app.ui.McBotAppUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ConsolePrinter {

    private JTextPane console;

    public ConsolePrinter() {
        McBotAppUI appUI = McBotAppUI.getInstance();
        if (appUI != null) {
            this.console = appUI.getConsole();
        }
    }

    // Normal Message
    public void NormalMessage(String message) {
        message = "✔ " + message + "\n";
        printeMessageToConsole(message);
    }

    // Print warning
    public void WarningMessage() {

    }

    // Print Error
    public void ErrorMessage() {

    }

    private void printeMessageToConsole(String message) {
        StyledDocument doc = console.getStyledDocument();
        Style style = console.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, Color.GREEN);
        try {
            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
