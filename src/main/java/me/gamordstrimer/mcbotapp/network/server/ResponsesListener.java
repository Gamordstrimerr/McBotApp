package me.gamordstrimer.mcbotapp.network.server;

import me.gamordstrimer.mcbotapp.network.client.LoginRequest;
import me.gamordstrimer.mcbotapp.utils.PacketReader;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.InflaterInputStream;

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
                System.out.println("Invalid packet received, stopping...");
                break;
            }

            // Read Packet ID (VarInt)
            int packetID = PacketReader.readVarInt(dataIn);
            int bytesRead = PacketReader.getLastReadVarIntSize(); // Get size of last VarInt read

            System.out.println("Received packet ID: 0x" + String.format("%02X", packetID) + " (Length: " + packetLength + ")");

            // Read the remaining packet data
            int remainingLength = packetLength - bytesRead;
            byte[] packetData = new byte[remainingLength];
            dataIn.readFully(packetData);

            // Decompress if packet length > 256 (compressed)
            if (packetLength > 256) {
                packetData = decompress(packetData);
                System.out.println("Decompressed Packet Data (Hex): " + bytesToHex(packetData));
                System.out.println("Decompressed Packet Data (ASCII): " + new String(packetData, "UTF-8"));
            } else {
                System.out.println("Raw Packet Data (Hex): " + bytesToHex(packetData));
                System.out.println("Raw Packet Data (ASCII): " + new String(packetData, "UTF-8"));
            }
        }
    }

    // Decompression method
    private byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(compressedData);
        InflaterInputStream inflater = new InflaterInputStream(byteIn);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inflater.read(buffer)) > 0) {
            byteOut.write(buffer, 0, len);
        }

        inflater.close();
        return byteOut.toByteArray();
    }

    // Utility method to convert byte array to a hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}