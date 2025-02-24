package me.gamordstrimer.network.packets.disconnect;

import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientDisconnectPacket64 {

    private SendPacket sendPacket;

    public void disconnect() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(buffer);

        // Write Packet ID.
        PacketWriter.writeVarInt(packet, 0x40);
        // Write Reason
        PacketWriter.writeString(packet, "client disconnected");

        sendPacket.sendPacket(buffer.toByteArray());
    }
}
