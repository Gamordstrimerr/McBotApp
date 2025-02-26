package me.gamordstrimer.network.packets.play.serverbound;

import me.gamordstrimer.network.config.StoreSocket;
import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ChatMessagePacket01 {

    private SendPacket sendPacket;
    private Socket socket;

    private OutputStream out;
    private ByteArrayOutputStream buffer;

    public ChatMessagePacket01() throws IOException {
        this.socket = StoreSocket.getInstance().getSocket();

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);

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
