package me.gamordstrimer.mcbotapp.network.server;

import me.gamordstrimer.mcbotapp.network.config.PacketCompression;
import me.gamordstrimer.mcbotapp.state.ConnectionState;
import me.gamordstrimer.mcbotapp.utils.PacketReader;
import me.gamordstrimer.mcbotapp.utils.PacketWriter;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;

public class ResponsesHandler {

    private Socket socket;

    private SendPacket sendPacket;
    private ConnectionState connectionState = ConnectionState.LOGIN;
    private PacketCompression packetCompression;

    private OutputStream out;
    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public ResponsesHandler(Socket socket) throws IOException {
        this.socket = socket;

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);

        this.buffer = new ByteArrayOutputStream();
        this.packet = new DataOutputStream(buffer);
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
                    packetCompression = new PacketCompression(compressionThreshold);
                    System.out.println("[SET_COMPRESSION] Threshold: " + compressionThreshold);
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
                    System.out.println("[RECEIVED KEEP_ALIVE] Received ID: " + keepAliveID);

                    // Respond to the Keep-Alive packet by sending back the same ID
                    sendKeepAliveResponse(keepAliveID);
                    break;
                case 0x02: // Chat message
                    // Handle chat messages (if necessary)
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

    private void sendKeepAliveResponse(int keepAliveID) throws IOException {
        buffer.reset(); // Reuse buffer
        packet = new DataOutputStream(buffer);

        // Write the Packet ID (0x00) for Keep-Alive
        PacketWriter.writeVarInt(packet, 0x00);
        // Write the Keep-Alive ID
        PacketWriter.writeVarInt(packet, keepAliveID);

        // Convert packet to byte array
        byte[] packetData = buffer.toByteArray();

        // Create final packet buffer with length prefix
        ByteArrayOutputStream finalBuffer = new ByteArrayOutputStream();
        DataOutputStream finalPacket = new DataOutputStream(finalBuffer);

        if (packetCompression != null && packetCompression.getCompression() > 0) {
            // Compression is enabled
            if (packetData.length < packetCompression.getCompression()) {
                // If packet is smaller than compression threshold, write 0 (no compression)
                PacketWriter.writeVarInt(finalPacket, packetData.length + 1); // Length including the "0"
                PacketWriter.writeVarInt(finalPacket, 0); // "0" means uncompressed
                System.out.println("[KEEP_ALIVE] Packet smaller than compression threshold.");
                finalPacket.write(packetData);
            } else {
                // Compress the packet (you need a compression utility, e.g., Zlib)
                byte[] compressedData = PacketWriter.compress(packetData);

                PacketWriter.writeVarInt(finalPacket, compressedData.length); // Compressed length
                finalPacket.write(compressedData); // Write compressed data
            }
        } else {
            // Compression is disabled, just write the length normally
            PacketWriter.writeVarInt(finalPacket, packetData.length);
            finalPacket.write(packetData);
        }

        // Get final packet bytes
        byte[] finalPacketData = finalBuffer.toByteArray();
        System.out.println("[DEBUG] Final Packet Size: " + finalPacketData.length);

        // Debug output
        StringBuilder hexString = new StringBuilder();
        for (byte b : finalPacketData) {
            hexString.append(String.format("%02X ", b));
        }
        System.out.println("[SEND KEEP_ALIVE RESPONSE] Packet Data: " + hexString.toString().trim());

        // Send packet
        sendPacket.sendPacket(finalPacketData);
    }
}