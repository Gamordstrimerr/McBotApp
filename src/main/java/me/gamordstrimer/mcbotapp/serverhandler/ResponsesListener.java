package me.gamordstrimer.mcbotapp.serverhandler;

import me.gamordstrimer.mcbotapp.utils.PacketReader;
import me.gamordstrimer.mcbotapp.utils.PacketWriter;
import me.gamordstrimer.mcbotapp.utils.SendPacket;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ResponsesListener {

    private String SERVER_ADDR;
    private int SERVER_PORTS;

    private Socket socket;
    private OutputStream out;
    private SendPacket sendPacket;

    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public ResponsesListener(Socket socket, String SERVER_ADDR, int SERVER_PORTS) throws IOException {
        this.socket = socket;

        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);
    }

    public void receiveResponse() throws IOException {
        InputStream in = socket.getInputStream();
        DataInputStream dataIn = new DataInputStream(in);

        while (true) {
            // Read Packet length (VarInt)
            int packetLength = PacketReader.readVarInt(dataIn);
            if (packetLength < 0) {
                System.out.println("Invalid packet Received");
                break;
            }

            // Read Packet ID (VarInt)
            int packetID = PacketReader.readVarInt(dataIn);
            System.out.println("Received packet ID: " + packetID + " (Length: " + packetLength + ")");

            switch (packetID) {
                case 0x00: // Disconnect
                    String disconnectMessage = PacketReader.readString(dataIn);
                    System.out.println("Disconnected: " + disconnectMessage);
                    return; // Stop Listening

                case 0x02: // Login Success
                    String uuid = PacketReader.readString(dataIn);
                    String username = PacketReader.readString(dataIn);
                    System.out.println("Login Success! UUID: " + uuid + ", Username: " + username);
                    break; // Continue listening for more packets

                case 0x03: // Keep-Alive
                    int keepAliveID = PacketReader.readVarInt(dataIn);
                    System.out.println("Received Keep-Alive Packet. ID: " + keepAliveID);

                    // Send back a Keep-Alive response
                    sendKeepAlive(keepAliveID);
                    break; // Continue listening for more packets

                default:
                    byte[] unknownPacketData = new byte[packetLength - 1];
                    dataIn.readFully(unknownPacketData);
                    System.out.println("Unrecognized packet ID: " + packetID + ", Data: " + Arrays.toString(unknownPacketData));
                    dataIn.skipBytes(packetLength - 1); // Skip remaining bytes.
            }
        }
    }

    private void sendKeepAlive(int keepAliveID) throws IOException {
        buffer = new ByteArrayOutputStream();
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x03); // Keep-Alive Packet ID
        PacketWriter.writeVarInt(packet, keepAliveID); // send Keep-Alive ID

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("Sent Keep-Alive response. ID: " + keepAliveID);
    }
}
