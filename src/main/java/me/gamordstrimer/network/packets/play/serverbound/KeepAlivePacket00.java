package me.gamordstrimer.network.packets.play.serverbound;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.packets.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeepAlivePacket00 {

    private SendPacket sendPacket;
    private ConsolePrinter consolePrinter;

    private ByteArrayOutputStream buffer;

    public KeepAlivePacket00(SendPacket sendPacket) {
        this.sendPacket = sendPacket;
        this.buffer = new ByteArrayOutputStream();
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void processKeepAlivePacket(DataInputStream dataIn) throws IOException {
        int keepAliveID = PacketReader.readVarInt(dataIn);

        // Send the response to the keep Alive Packet.
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        tempPacket.write(0x00);
        PacketWriter.writeVarInt(tempPacket, keepAliveID);

        byte[] packetContent = buffer.toByteArray();

        buffer.reset();
        DataOutputStream finalPacket = new DataOutputStream(buffer);

        finalPacket.write(0);
        finalPacket.write(packetContent);

        sendPacket.sendPacket(buffer.toByteArray());
        consolePrinter.NormalMessage("[KEEP_ALIVE_PACKET] Server ⟺ Client (ID: " + keepAliveID +")");
    }
}
