package me.gamordstrimer.network;

import lombok.Getter;
import lombok.Setter;
import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.LoopsManager;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.packets.login.CLIENT_Packet0x03_LOGIN;
import me.gamordstrimer.network.packets.play.*;
import me.gamordstrimer.network.packets.state.ConnectionState;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.SendPacket;

import java.io.*;
import java.net.Socket;

public class ResponsesHandler {
    private final LoopsManager loopsManager = LoopsManager.getInstance();

    @Setter
    private Socket socket;
    private PacketCompression packetCompression = PacketCompression.getInstance();
    private ConsolePrinter consolePrinter;

    @Getter private volatile boolean running = true; // Add this to control the loop

    public ResponsesHandler() {
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);

        while (loopsManager.isRunning()) {
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
                // System.out.println("ResponseHandler Loop Running.");
            } catch (IOException ex) {
                if (!running) break; // If stopping, exit loop
                consolePrinter.ErrorMessage("Error receiving response: " + ex.getMessage());
                loopsManager.stop(); // Stop the loop
            }
        }
    }

    private void handlePacket(int packetID, DataInputStream dataIn) throws IOException {
        int availableBytes; // Declare once at the start
        ConnectionState connectionState = loopsManager.getConnectionState(); // Get shared state

        if (connectionState == ConnectionState.LOGIN) {
            switch (packetID) {
                case 0x03: // Set Compression
                    new CLIENT_Packet0x03_LOGIN().handlePacket(dataIn);
                    break;
                case 0x02: // Login Successful
                    String uuid = PacketReader.readString(dataIn);
                    String username = PacketReader.readString(dataIn);

                    loopsManager.setConnectionState(ConnectionState.PLAY);

                    consolePrinter.NormalMessage("[LOGIN_SUCCESS] UUID: " + uuid + ", Username: " + username);
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
                case 0x08: // Position and Look
                    new CLIENT_Packet0x08_PLAY().handlePacket(dataIn);
                    break;
                case 0x12: // Entity Velocity
                    // new CLIENT_Packet0x12_PLAY().handlePacket(dataIn);
                    break;
                case 0x21: // Chunk Data
                    // new CLIENT_Packet0x21_PLAY().handlePacket(dataIn);
                    break;
                case 0x22: // Multi Block Change
                    System.out.println("[PACKET] packet 0x22 received.");
                    break;
                case 0x23: // Block Change
                    System.out.println("[PACKET] packet 0x23 received.");
                    break;
                case 0x40:
                    System.out.println("[PACKET] packet 0x40 received.");
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
}