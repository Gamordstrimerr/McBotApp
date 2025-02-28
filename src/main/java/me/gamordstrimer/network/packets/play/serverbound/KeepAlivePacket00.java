package me.gamordstrimer.network.packets.play.serverbound;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
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

    public void sendKeepAliveResponse(int keepAliveID) throws IOException {
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

        // System.out.println("[SENT_KEEP_ALIVE] Keep alive ID sent: " + keepAliveID);
        consolePrinter.NormalMessage("[SENT_KEEP_ALIVE] Keep alive ID sent: " + keepAliveID);
    }
}
