package me.gamordstrimer.mcbotapp.app;

import me.gamordstrimer.mcbotapp.config.BotConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.OutputStream;
import java.io.PrintStream;

public class AppFrame {

    JFrame frame;
    JTextField server_address_field;
    JTextField server_port_field;
    JTextField username_field;
    JButton connect_button;
    JTextArea console;

    Font sansSerif = new Font("SansSerif", Font.PLAIN, 10);
    Font monoSpaced = new Font("Monospaced", Font.PLAIN, 12);

    private BotConfig botConfig;

    public AppFrame() {

        frame = new JFrame("Minecraft Bot App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(815, 400);
        frame.setLayout(null);
        frame.setResizable(false);

        server_address_field = new JTextField();
        server_address_field.setBounds(10,25,180,25);
        server_address_field.setFont(sansSerif);
        setPlaceHolder(server_address_field, "> Server Address");

        server_port_field = new JTextField();
        server_port_field.setBounds(210,25,180,25);
        server_port_field.setFont(sansSerif);
        setPlaceHolder(server_port_field, "> Default server ports : 25565");

        username_field = new JTextField();
        username_field.setBounds(410,25,180,25);
        username_field.setFont(sansSerif);
        setPlaceHolder(username_field, "> username of the bot");

        connect_button = new JButton("connect");
        connect_button.addActionListener(new AppListener(server_address_field, server_port_field, username_field));
        connect_button.setFont(sansSerif);
        connect_button.setFocusable(false);
        connect_button.setBounds(610,25,180,25);

        console = new JTextArea();
        console.setEditable(false);
        console.setFont(monoSpaced);
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBounds(10, 70, 780, 280);

        redirectConsoleOutput(console);

        frame.add(server_address_field);
        frame.add(server_port_field);
        frame.add(username_field);
        frame.add(connect_button);
        frame.add(scrollPane);
        frame.setVisible(true);
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
