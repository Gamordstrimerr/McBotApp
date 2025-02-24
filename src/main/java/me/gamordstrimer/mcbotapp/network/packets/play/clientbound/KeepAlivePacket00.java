package me.gamordstrimer.mcbotapp.network.packets.play.clientbound;

import me.gamordstrimer.mcbotapp.utils.PacketWriter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeepAlivePacket00 {

    int keepAliveID;

    public void sendKeepAlivePacket(ByteArrayOutputStream buffer, int keepAliveID) throws IOException {
        buffer.reset();
        DataOutputStream packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x00);
        PacketWriter.writeVarInt(packet, keepAliveID);
    }
}
