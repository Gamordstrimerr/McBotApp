package me.gamordstrimer.app.controllers;

import me.gamordstrimer.network.config.ConnectionConfig;
import me.gamordstrimer.network.ClientSession;
import me.gamordstrimer.network.packets.play.clientbound.DisconnectPacket64;
import me.gamordstrimer.utils.SendPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class AppListener implements ActionListener {

    private JTextField server_address_field;
    private JTextField server_port_field;
    private JTextField username_field;

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;

    private ConnectionConfig connectionConfig;
    private ClientSession clientSession;

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
                connectionConfig = ConnectionConfig.getInstance();
                connectionConfig.setConnectionConfig(SERVER_ADDR, SERVER_PORTS, username);

                if (Objects.isNull(connectionConfig)) {
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
                System.out.println("> Server Address: " + connectionConfig.getSERVER_ADDR());
                System.out.println("> Server Ports: " + connectionConfig.getSERVER_PORTS());
                System.out.println("> Username of the Bot: " + connectionConfig.getUsername());

                // Initialize clientSession BEFORE starting the thread
                clientSession = new ClientSession(connectionConfig.getSERVER_ADDR(), connectionConfig.getSERVER_PORTS(), connectionConfig.getUsername());

                // Run connection in a separate thread
                new Thread(clientSession::connect).start();

            } else if (sourceButton.getText().equals("Disconnect")) {
                try {
                    DisconnectPacket64 disconnectPacket64 = new DisconnectPacket64();
                    disconnectPacket64.sendDisconnectPacket();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}