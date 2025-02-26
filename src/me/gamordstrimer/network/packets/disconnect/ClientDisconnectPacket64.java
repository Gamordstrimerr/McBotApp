package me.gamordstrimer.network.packets.disconnect;

import me.gamordstrimer.network.config.StoreSocket;
import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientDisconnectPacket64 {

    private SendPacket sendPacket;
    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public ClientDisconnectPacket64() throws IOException {
        Socket socket = StoreSocket.getInstance().getSocket();
        this.sendPacket = new SendPacket(socket.getOutputStream());

        this.buffer = new ByteArrayOutputStream();
        this.packet = new DataOutputStream(buffer);
    }

    public void disconnect() throws IOException {
        ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream();
        DataOutputStream tempPacket = new DataOutputStream(tempBuffer);

        // Write Packet ID.
        PacketWriter.writeVarInt(tempPacket, 0x40);
        // Write Reason
        PacketWriter.writeString(tempPacket, "client disconnected");

        byte[] packetContent = tempBuffer.toByteArray(); // Get the raw packet contents

        // Calculate total packet length (size of packetContent)
        ByteArrayOutputStream finalBuffer = new ByteArrayOutputStream();
        DataOutputStream finalPacket = new DataOutputStream(finalBuffer);

        int packetLength = packetContent.length;

        // Write total packet lenght as a VarInt
        PacketWriter.writeVarInt(finalPacket, packetLength);
        // Write the actual packet content
        finalPacket.write(packetContent);

        System.out.println("[DISCONNECT] Length: " + packetLength);

        sendPacket.sendPacket(buffer.toByteArray());
    }
}
