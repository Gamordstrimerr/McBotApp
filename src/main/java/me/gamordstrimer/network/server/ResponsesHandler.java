package me.gamordstrimer.network.server;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.packets.login.CLIENT_Packet0x03_LOGIN;
import me.gamordstrimer.network.packets.play.*;
import me.gamordstrimer.network.state.ConnectionState;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.SendPacket;

import java.io.*;
import java.net.Socket;

public class ResponsesHandler {

    private Socket socket;

    private SendPacket sendPacket;
    private ConnectionState connectionState = ConnectionState.LOGIN;
    private PacketCompression packetCompression = PacketCompression.getInstance();
    private ConsolePrinter consolePrinter;

    private volatile boolean running = true; // Add this to control the loop

    public ResponsesHandler() {
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);

        while (running) {
            try {
                // Read Packet length (VarInt)
                int packetLength = PacketReader.readVarInt(dataIn);
                if (packetLength < 0) {
                    System.out.println("Invalid packet received, stopping...");
                    break;
                }
                if (packetCompression != null && packetCompression.getCompression() > 0 ) {
                    // Read compressed packet
                    byte[] decompressedData = PacketReader.readCompressedPacket(dataIn, packetLength, packetCompression.getCompression());
                    DataInputStream decompressedStream = new DataInputStream(new ByteArrayInputStream(decompressedData));

                    int packetID = PacketReader.readVarInt(decompressedStream);
                    handlePacket(packetID, decompressedStream);
                } else {
                    // Read Packet ID (VarInt)
                    int packetID = PacketReader.readVarInt(dataIn);
                    handlePacket(packetID, dataIn);
                }
            } catch (IOException ex) {
                if (!running) break; // If stopping, exit loop
                consolePrinter.ErrorMessage("Error receiving response: " + ex.getMessage());
                stop(); // Stop the loop
            }
        }
    }

    private void handlePacket(int packetID, DataInputStream dataIn) throws IOException {
        int availableBytes; // Declare once at the start
        if (connectionState == ConnectionState.LOGIN) {
            switch (packetID) {
                case 0x03: // Set Compression
                    new CLIENT_Packet0x03_LOGIN().handlePacket(dataIn);
                    break;
                case 0x02: // Login Successful
                    String uuid = PacketReader.readString(dataIn);
                    String username = PacketReader.readString(dataIn);
                    connectionState = ConnectionState.PLAY;

                    consolePrinter.NormalMessage("[LOGIN_SUCCESS] UUID: " + uuid + ", Username: " + username);

                    // Log the state change
                    consolePrinter.NormalMessage("[STATE CHANGE] Connection state ⟹ PLAY.");
                    break;
                case 0x00: // Disconnected
                    String reason = PacketReader.readString(dataIn);
                    consolePrinter.ErrorMessage("[DISCONNECTED] Received disconnect packet during LOGIN state.");
                    consolePrinter.ErrorMessage("[REASON]: " + reason);
                    return;
                default:
                    // Skip unknown packet by reading and discarding remaining bytes
                    availableBytes = dataIn.available();
                    if (availableBytes > 0) {
                        dataIn.skipBytes(availableBytes);
                    }
                    consolePrinter.WarningMessage("[LOGIN] Unknown packet 0x" + String.format("%02X", packetID) + " received during LOGIN state.");
                    break;
            }
        } else if (connectionState == ConnectionState.PLAY) {
            switch (packetID) {
                case 0x00: // Keep-Alive Packet
                    new SERVER_Packet0x00_PLAY().handlePacket(dataIn);
                    break;
                case 0x01: // Join Game Packet
                    new CLIENT_Packet0x01_PLAY().handlePacket(dataIn);
                    break;
                case 0x02: // Chat message
                    new CLIENT_Packet0x02_PLAY().handlePacket(dataIn);
                    break;
                case 0x12: // Entity Velocity
                    new CLIENT_Packet0x12_PLAY().handlePacket(dataIn);
                    break;
                case 0x40:
                    System.out.println("receive 0x40 packet");
                    new CLIENT_Packet0x40_PLAY().handlePacket(dataIn);
                    break;
                default:
                    // Skip unknown packet by reading and discarding remaining bytes
                    availableBytes = dataIn.available();
                    if (availableBytes > 0) {
                        dataIn.skipBytes(availableBytes);
                    }
                    break;
            }
        }
    }

    public void restartLoop() {
        stop();
        running = true;
        consolePrinter.NormalMessage("Responses Handler restarted.");
        connectionState = ConnectionState.LOGIN;
    }

    public void stop() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            consolePrinter.ErrorMessage("Error closing socket: " + e.getMessage());
        }
    }
}