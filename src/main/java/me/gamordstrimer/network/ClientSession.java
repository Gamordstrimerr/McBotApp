package me.gamordstrimer.network;

import lombok.Getter;
import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.client.LoginRequest;
import me.gamordstrimer.network.config.StoreSocket;
import me.gamordstrimer.network.server.ResponsesHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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

    private boolean stopRequest = false;

    public ClientSession(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void connect() {
        while (!stopRequest) {
            try {
                // STEP 1 : Create Socket and connect to the server
                socket = new Socket();
                consolePrinter.WarningMessage("Attempting to connect to " + SERVER_ADDR + ":" + SERVER_PORTS);
                //System.out.println("Attempting to connect to " + SERVER_ADDR + ":" + SERVER_PORTS);
                socket.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORTS), 5000);
                StoreSocket storeSocket = StoreSocket.getInstance();
                storeSocket.setSocket(socket);
                System.out.println("Connected to the server");

                // STEP 2 : send Login Request packet
                LoginRequest loginRequest = new LoginRequest(socket, SERVER_ADDR, SERVER_PORTS);
                loginRequest.sendLoginRequest(username);

                // STEP 3 : Listen For Response(s)
                ResponsesHandler responsesHandler = new ResponsesHandler(socket);
                responsesHandler.receiveResponse();

            } catch (IOException ex) {
                System.out.println("Connection failed : " + ex.getMessage());
            } finally { // STEP 4 : close the socket
                closeConnection();
            }
        }
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException ex) {
            System.out.println("Error closing socket: " + ex.getMessage());
        }
    }

    public void stopConnection() {
        stopRequest = true;
    }
}