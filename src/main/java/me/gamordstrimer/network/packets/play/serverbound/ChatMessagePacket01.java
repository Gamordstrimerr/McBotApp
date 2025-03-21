package me.gamordstrimer.network.packets.play.serverbound;

import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.packets.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatMessagePacket01 {

    private SendPacket sendPacket;
    private ByteArrayOutputStream buffer;

    public ChatMessagePacket01() throws IOException {
        this.sendPacket = new SendPacket();

        this.buffer = new ByteArrayOutputStream();
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
