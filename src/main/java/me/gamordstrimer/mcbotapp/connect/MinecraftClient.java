package me.gamordstrimer.mcbotapp.connect;

import me.gamordstrimer.mcbotapp.login.LoginRequest;
import me.gamordstrimer.mcbotapp.serverhandler.ResponsesListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * Minecraft 1.8.8 (protocol version 47), handshake packet structure :
 *
 * +----------------------------------------------------------------------------------------+
 * |        Field      |         Type       |                     Notes                     |
 * +----------------------------------------------------------------------------------------+
 * | Packet ID         |    VarInt          | Always '0x00' for handshake                   |
 * +----------------------------------------------------------------------------------------+
 * | Protocol version  |    VarInt          | '47' for minecraft 1.8.8                      |
 * +----------------------------------------------------------------------------------------+
 * | Server Address    |    String          | The hostname or IP of the server              |
 * +----------------------------------------------------------------------------------------+
 * | Server Port       |    Unsigned Short  | The port number or the server (e.g., '25565') |
 * +----------------------------------------------------------------------------------------+
 * | Next State        |    VarInt          | '1' for status, '2' for login                 |
 * +----------------------------------------------------------------------------------------+
 **/

/** To Send Example Packet :
 byte[] handshakePacket = {0x00, 0x00, 0x00, 0x01}; //Example Packet
 out.write(handshakePacket);
 out.flush();
 System.out.println("Handshake packet sent!");
 */

public class MinecraftClient {

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;
    private Socket socket;

    public MinecraftClient(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
    }

    public void connect() {
        try {
            // STEP 1 : Create Socket and connect to the server
            socket = new Socket();
            System.out.println("Attempting to connect to " + SERVER_ADDR + ":" + SERVER_PORTS);
            socket.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORTS), 5000);
            System.out.println("Connected to the server");

            // STEP 2 : send Login Request packet
            LoginRequest loginRequest = new LoginRequest(socket, SERVER_ADDR, SERVER_PORTS);
            loginRequest.sendLoginRequest(username);

            // STEP 3 : Listen For Response(s)
            ResponsesListener responsesListener = new ResponsesListener(socket, SERVER_ADDR, SERVER_PORTS);
            responsesListener.receiveResponse();


        } catch (IOException ex) {
            System.out.println("Connection failed : " + ex.getMessage());
        } finally {
            closeConnection();
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

}
