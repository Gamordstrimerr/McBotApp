package me.gamordstrimer.network.server;

import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.state.ConnectionState;
import me.gamordstrimer.utils.PacketReader;
import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

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
                    packetCompression = PacketCompression.getInstance();
                    packetCompression.setCompression(compressionThreshold);
                    System.out.println("[SET_COMPRESSION] Threshold: " + packetCompression.getCompression());
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
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        tempPacket.write(0x00);
        PacketWriter.writeVarInt(tempPacket, keepAliveID);

        byte[] packetContent = buffer.toByteArray();

        buffer.reset();
        DataOutputStream finalePacket = new DataOutputStream(buffer);

        finalePacket.write(0);
        finalePacket.write(packetContent);

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("[KEEP_ALIVE_RESPONSE] Keep alive ID sent: " + keepAliveID);
    }
}