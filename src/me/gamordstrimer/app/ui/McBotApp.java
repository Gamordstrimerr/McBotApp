package me.gamordstrimer.app.ui;

import me.gamordstrimer.app.AppListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class McBotApp extends JFrame{

    private JPanel panelMain;
    private JTextField server_ports;
    private JTextField server_addr;
    private JTextField bot_username;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextArea console;
    private JTextArea server_console;

    public McBotApp() {
        setTitle("MINECRAFT BOT APPLICATION");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        setContentPane(panelMain);

        setPlaceHolder(server_addr, "> Server Address");
        setPlaceHolder(server_ports, "> Default server ports : 25565");
        setPlaceHolder(bot_username, "> username of the bot");

        connectButton.addActionListener(new AppListener(server_addr, server_ports, bot_username));
        connectButton.setFocusable(false);

        disconnectButton.setFocusable(false);

        console.setEditable(false);
        server_console.setEditable(false);

        setVisible(true);
    }

    private void setPlaceHolder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY); // Set the PlaceHolder color

        // Focus listener to clear text on focus if it's the placeholder
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set text color to black when typing
                }
            }

            public void focusLost(FocusEvent event) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY); // Restore placeholder color
                }
            }

        });
    }
}
