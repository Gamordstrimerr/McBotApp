package me.gamordstrimer.mcbotapp.network.client;

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

        this.buffer = new ByteArrayOutputStream();
        this.packet = new DataOutputStream(buffer);
    }

    public void sendLoginRequest(String username) throws IOException {
        try {
            // Step 1: Send Handshake Packet
            sendHandshakePacket();

            // Step 2: Send Login Start Packet
            sendLoginStartPacket(username);
        } catch (IOException ex) {
            throw new LoginRequestException("Failed to send login request for username: " + username, ex);
        }
    }

    /**
     * Minecraft 1.8.8 (protocol version 47), handshake packet structure :
     *
     * +----------------------------------------------------------------------------------------+
     * |        Field      |         Type       |                     Notes                     |
     * +----------------------------------------------------------------------------------------+
     * | Packet ID         |    VarInt          | Always '0x00' for handshake                   |
     * +----------------------------------------------------------------------------------------+
     * | Protocol version  |    VarInt          | '47' for minecraft 1.8.8                      |
     * +----------------------------------------------------------------------------------------+
     * | Server Address    |    String          | The hostname or IP of the server              |
     * +----------------------------------------------------------------------------------------+
     * | Server Port       |    Unsigned Short  | The port number or the server (e.g., '25565') |
     * +----------------------------------------------------------------------------------------+
     * | Next State        |    VarInt          | '1' for status, '2' for login                 |
     * +----------------------------------------------------------------------------------------+
     **/

    private void sendHandshakePacket() throws IOException {
        buffer.reset(); // Reuse Buffer
        packet = new DataOutputStream(buffer);

        // Packet fields
        int protocolVersion = 47; // Minecraft 1.8.8 protocol version.
        int nextState = 2; //Status request for Login.

        // Write packet data
        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Handshake)
        PacketWriter.writeVarInt(packet, protocolVersion);
        PacketWriter.writeString(packet, SERVER_ADDR);
        packet.writeShort(SERVER_PORTS);
        PacketWriter.writeVarInt(packet, nextState);

        // Converte to byte array and send
        sendPacket.sendPacket(buffer.toByteArray());
        System.out.println("Handshake packet send!");
    }

    private void sendLoginStartPacket(String username) throws IOException {
        buffer.reset(); // Reuse Buffer
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Login Start)
        PacketWriter.writeString(packet, username); // Player's Username

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("Login Request sent for user: " + username);
    }
}
