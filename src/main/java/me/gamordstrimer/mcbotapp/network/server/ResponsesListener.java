package me.gamordstrimer.mcbotapp.network.server;

import me.gamordstrimer.mcbotapp.network.client.LoginRequest;
import me.gamordstrimer.mcbotapp.utils.PacketReader;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

public class ResponsesListener {

    private String SERVER_ADDR;
    private int SERVER_PORTS;

    private Socket socket;
    private OutputStream out;

    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public ResponsesListener(Socket socket, String SERVER_ADDR, int SERVER_PORTS) throws IOException {
        this.socket = socket;

        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;

        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);

        while (true) {
            // Read Packet length (VarInt)
            int packetLength = PacketReader.readVarInt(dataIn);
            if (packetLength < 0) {
                System.out.println("Invalid packet Received");
                break;
            }

            // Read Packet ID (VarInt)
            int packetID = PacketReader.readVarInt(dataIn);
            System.out.println("Received packet ID: " + packetID + " (Length: " + packetLength + ")");

            switch (packetID) {
                case 0x00: // Disconnect
                    String disconnectID = PacketReader.readString(dataIn);
                    System.out.println("Disconnected: " + disconnectID);

                    if (packetLength <= 1) {
                        System.out.println("Packet too short, skipping...");
                        return;
                    }

                    byte[] packetData = new byte[packetLength - 1];
                    dataIn.readFully(packetData);

                    System.out.println("Raw disconnect message data: " + Arrays.toString(packetData));
                    String disconnectMessage = new String(packetData, "UTF-8");
                    System.out.println("Decoded Disconnect Message: " + disconnectMessage);

                    // Try to reconnect
                    System.out.println("Attempting to reconnect...");
                    // reconnect();  // Your reconnect logic here
                    return; // Stop listening once disconnected

                case 0x02: // Login Success
                    UUID uuid = PacketReader.readUUID(dataIn);
                    String username = PacketReader.readString(dataIn);
                    System.out.println("Login Success! UUID: " + uuid + ", Username: " + username);
                    break; // Continue Listening for other packets

                case 0x03: // Get Compression Threshold
                    System.out.println("Compression Threshold Packet ID. Skipping...");
                    dataIn.skipBytes(packetLength);
                    break;

                default:
                    // Default case: Handle any unrecognized packets
                    System.out.println("Unrecognized packet ID: " + packetID + ". Skipping...");
                    dataIn.skipBytes(packetLength); // Skip unrecognized packets
                    break;
            }
        }
    }

    private void reconnect() {
        try {
            socket.close(); // Close the current socket
            Thread.sleep(5000); // Wait for 5 seconds before trying to reconnect
            System.out.println("Reconnecting...");
            socket = new Socket(SERVER_ADDR, SERVER_PORTS);  // Create a new socket
            LoginRequest loginRequest = new LoginRequest(socket, SERVER_ADDR, SERVER_PORTS);
            loginRequest.sendLoginRequest("BOT1"); // Re-send handshake and login request
        } catch (IOException | InterruptedException e) {
            System.out.println("Reconnection failed: " + e.getMessage());
        }
    }
}