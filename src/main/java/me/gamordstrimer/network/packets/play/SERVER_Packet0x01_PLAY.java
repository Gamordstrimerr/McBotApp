package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SERVER_Packet0x01_PLAY extends Packet {

    private ByteArrayOutputStream buffer;

    public SERVER_Packet0x01_PLAY() {
        super(ConnectionState.PLAY);

        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public Integer setPacketID() {
        return 0x01;
    }

    @Override
    public String setName() {
        return "Send_Chat_Message_Packet";
    }

    public void sendChatPacket(String chatMessage) {
        try {
            sendChatMessage(chatMessage);
        } catch (IOException ex) {
            System.out.println("[CHAT] Error " + ex.getMessage());
        }
    }

    private void sendChatMessage(String chatMessage) throws IOException {
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        tempPacket.write(01);
        PacketWriter.writeString(tempPacket, chatMessage);

        byte[] packetContent = buffer.toByteArray();
        byte[] compressedPacket = PacketWriter.compress(packetContent);

        buffer.reset();
        DataOutputStream finalPacket = new DataOutputStream(buffer);

        finalPacket.write(compressedPacket);

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("[CHAT] message: " + chatMessage);
    }
}
