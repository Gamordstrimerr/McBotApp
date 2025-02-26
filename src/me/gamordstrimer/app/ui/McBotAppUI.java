package me.gamordstrimer.app.ui;

import lombok.Getter;
import me.gamordstrimer.app.controllers.AppListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.OutputStream;
import java.io.PrintStream;

public class McBotAppUI extends JFrame{

    @Getter private static McBotAppUI instance;

    private JPanel panelMain;
    private JTextField server_ports;
    private JTextField server_addr;
    private JTextField bot_username;
    private JButton connectButton;
    private JButton disconnectButton;
    @Getter private JTextPane console;
    @Getter private JTextPane server_console;
    private JTextField chat_input;
    private JButton chat_Button;

    public McBotAppUI() {
        instance = this;

        setTitle("MINECRAFT BOT APPLICATION");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 600);
        setLocationRelativeTo(null);

        setContentPane(panelMain);

        setPlaceHolder(server_addr, "> Server Address");
        setPlaceHolder(server_ports, "> Default server ports : 25565");
        setPlaceHolder(bot_username, "> username of the bot");
        setPlaceHolder(chat_input,"> send a message </>");

        connectButton.addActionListener(new AppListener(server_addr, server_ports, bot_username, chat_input));
        connectButton.setFocusable(false);

        disconnectButton.addActionListener(new AppListener(server_addr, server_ports, bot_username, chat_input));
        disconnectButton.setFocusable(false);

        chat_Button.addActionListener(new AppListener(server_addr, server_ports, bot_username, chat_input));
        chat_Button.setFocusable(false);

        console.setEditable(false);
        // redirectConsoleOutput(console);

        server_console.setEditable(false);

        setVisible(true);
    }

    private void setPlaceHolder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.decode("#A69CAC")); // Set the PlaceHolder color

        // Focus listener to clear text on focus if it's the placeholder
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE); // Set text color to black when typing
                }
            }

            public void focusLost(FocusEvent event) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.decode("#A69CAC")); // Restore placeholder color
                }
            }

        });
    }

    private void redirectConsoleOutput(JTextArea consoleArea) {
        OutputStream outStream = new OutputStream() {
            @Override
            public void write(int b) {
                consoleArea.append(String.valueOf((char) b));
                consoleArea.setCaretPosition(consoleArea.getDocument().getLength()); // Auto-scroll
            }
        };

        PrintStream consoleStream = new PrintStream(outStream, true);
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }
}
