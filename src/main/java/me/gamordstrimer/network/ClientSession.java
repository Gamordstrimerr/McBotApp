package me.gamordstrimer.network;

import lombok.Getter;
import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.controllers.ServerConsolePrinter;
import me.gamordstrimer.network.client.LoginRequest;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.config.StoreSocket;
import me.gamordstrimer.network.server.ResponsesHandler;

import java.io.IOException;
import java.net.*;

/**
 * To instantiate the connection between the client and the server, minecraft use TCP's three way handshake.
 * - SYN -> client send a SYN (synchronize) packet to the server to start the connection.
 * - SYN-ACK -> server responds with a SYN-ACK (synchronize-acknowledge) packet.
 * - ACK -> client replies with an ACK (acknowledge) packet, establishing the connection.
 *
 * After this, client-server begins exchanging packets for login auth and game data.
 *
 **/

public class ClientSession {

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;
    @Getter private Socket socket;

    private ConsolePrinter consolePrinter;
    private ServerConsolePrinter serverConsolePrinter;
    private ResponsesHandler responsesHandler;

    public ClientSession() {
        this.consolePrinter = ConsolePrinter.getInstance();
        this.serverConsolePrinter = ServerConsolePrinter.getInstance();
        this.responsesHandler = new ResponsesHandler();
    }

    public void setServerInfos(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
    }

    public void connect() {
        try {
            // STEP 1 : Create Socket and connect to the server
            socket = new Socket();
            consolePrinter.clearConsole();
            consolePrinter.WarningMessage("Attempting to connect to " + SERVER_ADDR + ":" + SERVER_PORTS);
            //System.out.println("Attempting to connect to " + SERVER_ADDR + ":" + SERVER_PORTS);
            socket.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORTS), 5000);
            StoreSocket storeSocket = StoreSocket.getInstance();
            storeSocket.setSocket(socket);
            // System.out.println("Connected to the server");
            consolePrinter.NormalMessage("Connection established with the server");

            // STEP 2 : send Login Request packet
            LoginRequest loginRequest = new LoginRequest(socket, SERVER_ADDR, SERVER_PORTS);
            loginRequest.sendLoginRequest(username);

            responsesHandler.restartLoop(); // restart the loop to listen for packet.

            // STEP 3 : Listen For Response(s)
            responsesHandler.setSocket(socket);
            responsesHandler.receiveResponse();

            consolePrinter.ErrorMessage("Disconnecting ...");

        } catch (IOException ex) {
            consolePrinter.ErrorMessage("I/O Error: " + ex.getMessage());
        } catch (Exception ex) {
            consolePrinter.ErrorMessage("Unexpected error: " + ex.getMessage());
        }
    }

    public void closeSocket() {
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (responsesHandler != null) {
                responsesHandler.stop(); // Stop the loop before closing the socket
            }
            if (socket != null) {
                if (!socket.isClosed()) {
                    socket.close();
                }
                socket = null; // Ensure socket is reset
            }

            PacketCompression.getInstance().resetCompression();

            serverConsolePrinter.clearServerConsole();
            consolePrinter.clearConsole();
            consolePrinter.ErrorMessage("Connection closed.");
        } catch (IOException ex) {
            // System.out.println("Error closing socket: " + ex.getMessage());
            consolePrinter.ErrorMessage("Error closing socket: " + ex.getMessage());
        }
    }
}