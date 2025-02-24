package me.gamordstrimer.app.controllers;

import me.gamordstrimer.network.config.BotConfig;
import me.gamordstrimer.network.ClientSession;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class AppListener implements ActionListener {

    private JTextField server_address_field;
    private JTextField server_port_field;
    private JTextField username_field;

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;

    private BotConfig botConfig;

    public AppListener(JTextField server_address_field, JTextField server_port_field, JTextField username_field) {
        this.server_address_field = server_address_field;
        this.server_port_field = server_port_field;
        this.username_field = username_field;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Check if the source of the event is a JButton
        if (event.getSource() instanceof JButton) {
            JButton sourceButton = (JButton) event.getSource();
            if (sourceButton.getText().equals("Connect")) {
                if (server_address_field.getText().isEmpty() || server_address_field.getText().equals("> Server Address")) {
                    SERVER_ADDR = "";
                } else {
                    SERVER_ADDR = server_address_field.getText();
                }

                if (server_port_field.getText().isEmpty() || server_port_field.getText().equals("> Default server ports : 25565")) {
                    SERVER_PORTS = 25565;
                } else {
                    SERVER_PORTS = Integer.parseInt(server_port_field.getText());
                }

                if (username_field.getText().isEmpty() || username_field.getText().equals("> username of the bot")) {
                    username = "";
                } else {
                    username = username_field.getText();
                }

                // Store the value in BotConfig.
                botConfig = new BotConfig(SERVER_ADDR, SERVER_PORTS, username);

                if (Objects.isNull(botConfig)) {
                    System.out.println("BotConfig isn't available yet.");
                    return;
                }

                if (SERVER_ADDR == null || SERVER_ADDR.isEmpty()) {
                    System.out.println("Can't Start without: server address.");
                    return;
                }

                if (username == null || username.isEmpty()) {
                    System.out.println("Can't Start without: username.");
                    return;
                }
                System.out.println("> Server Address: " + botConfig.getSERVER_ADDR());
                System.out.println("> Server Ports: " + botConfig.getSERVER_PORTS());
                System.out.println("> Username of the Bot: " + botConfig.getUsername());

                // Run connection in a separate thread
                new Thread(() -> {
                    ClientSession client = new ClientSession(botConfig.getSERVER_ADDR(), botConfig.getSERVER_PORTS(), botConfig.getUsername());
                    client.connect();
                }).start();
            } else if (sourceButton.getText().equals("Disconnect")) {
                System.out.println("Disconnect button press");
            }
        }
    }
}
