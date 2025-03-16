package me.gamordstrimer.network.server;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.packets.login.clientbound.SetCompressionPacket03;
import me.gamordstrimer.network.packets.play.clientbound.*;
import me.gamordstrimer.network.packets.play.serverbound.KeepAlivePacket00;
import me.gamordstrimer.network.state.ConnectionState;
import me.gamordstrimer.utils.PacketReader;
import me.gamordstrimer.utils.SendPacket;

import java.io.*;
import java.net.Socket;

public class ResponsesHandler {

    private Socket socket;

    private SendPacket sendPacket;
    private ConnectionState connectionState = ConnectionState.LOGIN;
    private PacketCompression packetCompression = PacketCompression.getInstance();
    private ConsolePrinter consolePrinter;

    private OutputStream out;

    private volatile boolean running = true; // Add this to control the loop

    public ResponsesHandler() {
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.sendPacket = new SendPacket(out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                    int compressionThreshold = PacketReader.readVarInt(dataIn);
                    SetCompressionPacket03.setCompression(compressionThreshold);
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
                    new KeepAlivePacket00(sendPacket).processKeepAlivePacket(dataIn);
                    break;
                case 0x01: // Join Game Packet
                    new JoinGamePacket01().processJoinGamePacket(dataIn);
                    break;
                case 0x02: // Chat message
                    String chatMessage = PacketReader.readString(dataIn);
                    new ChatMessagePacket02().processIncomingMessages(chatMessage);
                    break;
                case 0x08:
                    new PlayerPositionAndLookPacket08().handlePlayerPositionAndLook(dataIn);
                    break;
                case 0x12: // Entity Velocity
                    new EntityVelocityPacket18(sendPacket).handlePlayerVelocity(dataIn);
                    break;
                case 0x40:
                    System.out.println("receive 0x40 packet");
                    new DisconnectPacket64().processDisconnection(dataIn);
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