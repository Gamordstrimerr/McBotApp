package me.gamordstrimer.network.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gamordstrimer.app.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.packets.login.clientbound.SetCompressionPacket03;
import me.gamordstrimer.network.packets.play.clientbound.ChatMessagePacket02;
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

    public ResponsesHandler(Socket socket) throws IOException {
        this.socket = socket;

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);
        this.consolePrinter = new ConsolePrinter();
    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);

        while (true) {
            // Read Packet length (VarInt)
            int packetLength = PacketReader.readVarInt(dataIn);
            if (packetLength < 0) {
                System.out.println("Invalid packet received, stopping...");
                break;
            }

            if (packetCompression != null && packetCompression.getCompression() > 0 ) {
                // Read compressed packet
                byte[] decompressedData = PacketReader.readCompressedPacket(dataIn, packetLength, packetCompression.getCompression());

                // Wrap the decompressed data in a new DataInputStream
                DataInputStream decompressedStream = new DataInputStream(new ByteArrayInputStream(decompressedData));

                int packetID = PacketReader.readVarInt(decompressedStream);
                // System.out.println("Received decompressed packet ID: 0x" + String.format("%02X", packetID) + " (Length: " + decompressedData.length + ")");

                handlePacket(packetID, decompressedStream);
            } else {
                // Read Packet ID (VarInt)
                int packetID = PacketReader.readVarInt(dataIn);
                int bytesRead = PacketReader.getLastReadVarIntSize();

                // System.out.println("Received packet ID: 0x" + String.format("%02X", packetID) + " (Length: " + packetLength + ")");

                handlePacket(packetID, dataIn);
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

                    System.out.println("[LOGIN_SUCCESS] UUID: " + uuid + ", Username: " + username);

                    // Log the state change
                    System.out.println("[STATE CHANGE] Connection state changed to PLAY.");
                    break;
                case 0x00: // Disconnected
                    String reason = PacketReader.readString(dataIn);
                    System.out.println("[DISCONNECTED] Received disconnect packet during LOGIN state.");
                    System.out.println("[REASON]: " + reason);
                    break;
                default:
                    // Skip unknown packet by reading and discarding remaining bytes
                    availableBytes = dataIn.available();
                    if (availableBytes > 0) {
                        dataIn.skipBytes(availableBytes);
                    }
                    System.out.println("[LOGIN] Unknown packet 0x" + String.format("%02X", packetID) + " received during LOGIN state.");
                    break;
            }
        } else if (connectionState == ConnectionState.PLAY) {
            switch (packetID) {
                case 0x00: // Keep-Alive Packet
                    int keepAliveID = PacketReader.readVarInt(dataIn);
                    consolePrinter.NormalMessage("[RECEIVED_KEEP_ALIVE] Received ID: " + keepAliveID);

                    // Respond to the Keep-Alive packet by sending back the same ID
                    KeepAlivePacket00 keepAlivePacket00 = new KeepAlivePacket00(sendPacket);
                    keepAlivePacket00.sendKeepAliveResponse(keepAliveID);
                    break;
                case 0x02: // Chat message
                    String chatMessage = PacketReader.readString(dataIn);
                    ChatMessagePacket02 chatMessagePacket02 = new ChatMessagePacket02();
                    chatMessagePacket02.debugChatMessage(chatMessage);
                    // chatMessagePacket02.processIncomingMessages(chatMessage);
                    break;
                case 0x40: // Disconnect packet
                    String reason = PacketReader.readString(dataIn);
                    System.out.println("[DISCONNECTED] Reason: " + reason);
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