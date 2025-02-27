package me.gamordstrimer.app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import me.gamordstrimer.network.ClientSession;
import me.gamordstrimer.network.config.ConnectionConfig;
import me.gamordstrimer.network.packets.play.serverbound.ChatMessagePacket01;

import java.io.IOException;
import java.util.Objects;

public class AppController {

    @FXML private TextField address_field;
    @FXML private TextField ports_field;
    @FXML private TextField username_field;
    @FXML private TextField chat_input_field;
    @FXML private TextFlow console;
    @FXML private ScrollPane consoleScrollPane;

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String USERNAME;
    private String chat_message;

    private ConnectionConfig connectionConfig;
    private ClientSession clientSession;

    @FXML
    public void initialize() {
        // Initialize the other class and pass the reference of TextFlow
        ConsolePrinter.getInstance().setConsoleComponents(console, consoleScrollPane);
    }

    public void connect(ActionEvent event) {
        if (address_field.getText().isEmpty()) {
            SERVER_ADDR = "";
        } else {
            SERVER_ADDR = address_field.getText();
        }

        if (ports_field.getText().isEmpty()) {
            SERVER_PORTS = 25565;
        } else {
            try {
                SERVER_PORTS = Integer.parseInt(ports_field.getText());
            } catch (NumberFormatException ex) {
                System.out.println("[ERROR] You need to enter a Number for the server ports.");
                return;
            }catch (Exception ex) {
                System.out.println("[ERROR]: " + ex);
                return;
            }

        }

        if (username_field.getText().isEmpty()) {
            USERNAME = "";
        } else {
            USERNAME = username_field.getText();
        }

        // Store the value in BotConfig.
        connectionConfig = ConnectionConfig.getInstance();
        connectionConfig.setConnectionConfig(SERVER_ADDR, SERVER_PORTS, USERNAME);

        if (Objects.isNull(connectionConfig)) {
            System.out.println("[ERROR] BotConfig isn't available yet.");
            return;
        }
        if (SERVER_ADDR == null || SERVER_ADDR.isEmpty()) {
            System.out.println("[ERROR] Can't Start without a server address.");
            return;
        }
        if (USERNAME == null || USERNAME.isEmpty()) {
            System.out.println("[ERROR] Can't Start without a username.");
            return;
        }
        System.out.println("> Server Address: " + connectionConfig.getSERVER_ADDR());
        System.out.println("> Server Ports: " + connectionConfig.getSERVER_PORTS());
        System.out.println("> Username of the Bot: " + connectionConfig.getUsername());

        // Initialize clientSession BEFORE starting the thread
        clientSession = new ClientSession(connectionConfig.getSERVER_ADDR(), connectionConfig.getSERVER_PORTS(), connectionConfig.getUsername());
        // Run connection in a separate thread
        new Thread(clientSession::connect).start();
    }

    public void disconnect(ActionEvent event) {
        if (clientSession != null) {
            System.out.println("Disconnecting ...");
            clientSession.stopConnection(); // signal the thread to stop.
        } else {
            System.out.println("[ERROR] No active session to disconnect.");
        }
    }

    public void sendChat(ActionEvent event) {
        if (chat_input_field.getText().isEmpty()) {
            chat_message = "";
        } else {
            chat_message = chat_input_field.getText();
        }

        if (chat_message == null || chat_message.isEmpty()) {
            System.out.println("[ERROR] You can't send an empty message to the server!");
            return;
        }

        try {
            ChatMessagePacket01 chatMessagePacket01 = new ChatMessagePacket01();
            chatMessagePacket01.sendChatPacket(chat_message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[ACTION] send chat");
    }
}
