package me.gamordstrimer.mcbotapp.connect;

import me.gamordstrimer.mcbotapp.utils.PacketWriter;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;

public class Handshake {

    private String SERVER_ADDR;
    private int SERVER_PORTS;

    private Socket socket;
    private OutputStream out;

    public Handshake(Socket socket, String SERVER_ADDR, int SERVER_PORTS) throws IOException{
        this.socket = socket;

        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;

        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendHandshake() throws IOException {
        SendPacket sendPacket = new SendPacket(out);
        ByteArrayOutputStream buffer;
        DataOutputStream packet;

        // Packet fields
        int protocolVersion = 47; // Minecraft 1.8.8 protocol version.
        int nextState = 1; //Status request.

        // Step 1: Send Handshake Packet
        buffer = new ByteArrayOutputStream();
        packet = new DataOutputStream(buffer);

        // Write packet data
        PacketWriter.writeVarInt(packet, 0x00);
        PacketWriter.writeVarInt(packet, protocolVersion);
        PacketWriter.writeString(packet, SERVER_ADDR);
        packet.writeShort(SERVER_PORTS);
        PacketWriter.writeVarInt(packet, nextState);

        // Converte to byte array and send
        sendPacket.sendPacket(buffer.toByteArray());
        System.out.println("Handshake packet sent!");

        buffer = new ByteArrayOutputStream();
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x00); // Status Request (Packet ID -> 0x00)
        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("Status request packet sent!");

    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
            if (bytesRead < buffer.length) break; // Stop Reading when the full response is received
        }

        byte[] responseBytes = responseBuffer.toByteArray();
        if (responseBytes.length > 0) {
            String response = new String(responseBytes, "UTF-8");
            System.out.println("Server Response: " + response);
        } else {
            System.out.println("No response received from the server");
        }
    }
}
