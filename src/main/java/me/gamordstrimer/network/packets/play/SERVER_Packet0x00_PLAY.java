package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SERVER_Packet0x00_PLAY extends Packet {

    private ByteArrayOutputStream buffer;

    public SERVER_Packet0x00_PLAY() {
        super(ConnectionState.PLAY);

        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public Integer setPacketID() {
        return 0x00;
    }

    @Override
    public String setName() {
        return "Keep_Alive_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        int keepAliveID = PacketReader.readVarInt(dataIn);

        // Send the response to the keep Alive Packet.
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(tempPacket, packetID);
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
