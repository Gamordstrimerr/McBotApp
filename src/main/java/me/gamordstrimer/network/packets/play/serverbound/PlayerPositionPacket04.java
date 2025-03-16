package me.gamordstrimer.network.packets.play.serverbound;

import me.gamordstrimer.utils.PacketWriter;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerPositionPacket04 {

    private double x, y, z;
    private boolean onGround;

    private SendPacket sendPacket;
    private ByteArrayOutputStream buffer;

    public PlayerPositionPacket04(double x, double y, double z, boolean onGround, SendPacket sendPacket) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;

        this.sendPacket = sendPacket;
        this.buffer = new ByteArrayOutputStream();
    }

    public void processPlayerPosition() throws IOException {

        // Send the PlayerPositionUpdate
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);

        tempPacket.write(0X04);
        tempPacket.writeDouble(x);
        tempPacket.writeDouble(y);
        tempPacket.writeDouble(z);
        tempPacket.writeBoolean(onGround);

        byte[] packetContent = buffer.toByteArray();

        buffer.reset();
        DataOutputStream finalPacket = new DataOutputStream(buffer);

        finalPacket.write(0);
        finalPacket.write(packetContent);

        sendPacket.sendPacket(buffer.toByteArray());
        System.out.println("Position Updated");
    }
}
