package me.gamordstrimer.mcbotapp.login;

import me.gamordstrimer.mcbotapp.utils.PacketWriter;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;

public class LoginRequest {

    private String SERVER_ADDR;
    private int SERVER_PORTS;

    private Socket socket;
    private OutputStream out;
    private SendPacket sendPacket;

    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public LoginRequest(Socket socket, String SERVER_ADDR, int SERVER_PORTS) throws IOException {
        this.socket = socket;

        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);
    }

    public void sendLoginRequest(String username) throws IOException {

        // Packet fields
        int protocolVersion = 47; // Minecraft 1.8.8 protocol version.
        int nextState = 2; //Status request for Login.

        // Step 1: Send Handshake Packet
        buffer = new ByteArrayOutputStream();
        packet = new DataOutputStream(buffer);

        // Write packet data
        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Handshake)
        PacketWriter.writeVarInt(packet, protocolVersion);
        PacketWriter.writeString(packet, SERVER_ADDR);
        packet.writeShort(SERVER_PORTS);
        PacketWriter.writeVarInt(packet, nextState);

        // Converte to byte array and send
        sendPacket.sendPacket(buffer.toByteArray());

        // Step 2: Send Login Start Packet
        buffer = new ByteArrayOutputStream();
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Login Start)
        PacketWriter.writeString(packet, username); // Player's Username

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("Login Request sent for user: " + username);
    }
}
