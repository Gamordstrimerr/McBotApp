package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.network.config.StoreSocket;
import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class DisconnectPacket64 {

    private String reason = "Goodbye";

    private SendPacket sendPacket;
    private Socket socket;

    private OutputStream out;
    private ByteArrayOutputStream buffer;

    public DisconnectPacket64() throws IOException {
        this.socket = StoreSocket.getInstance().getSocket();

        this.out = new DataOutputStream(socket.getOutputStream());
        this.sendPacket = new SendPacket(out);

        this.buffer = new ByteArrayOutputStream();
    }

    public void sendDisconnectPacket()  {
        try {
            packetBuilding();
        } catch (IOException ex) {
            System.out.println("[@] Error: " + ex.getMessage());
        }
    }

    private void packetBuilding() throws IOException{
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        tempPacket.writeByte(0x40);
        PacketWriter.writeString(tempPacket, reason);

        byte[] packetContent = buffer.toByteArray();

        buffer.reset();
        DataOutputStream finalPacket = new DataOutputStream(buffer);

        finalPacket.write(0);
        finalPacket.write(packetContent);

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("[DISCONNECT] Client has been Disconnected.");
    }
}
