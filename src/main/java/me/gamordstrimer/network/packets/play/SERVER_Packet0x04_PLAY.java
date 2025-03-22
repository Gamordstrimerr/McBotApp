package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SERVER_Packet0x04_PLAY extends Packet {

    private ByteArrayOutputStream buffer;

    public SERVER_Packet0x04_PLAY() {
        super(ConnectionState.PLAY);

        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public Integer setPacketID() {
        return 0x04;
    }

    @Override
    public String setName() {
        return "Position_Packet_ServerBound";
    }

    public void sendPositionUpdate(double x, double y, double z, boolean onGround) {
        buffer.reset();
        DataOutputStream tempPacket = new DataOutputStream(buffer);
        try {
            tempPacket.write(0x04);
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
            System.out.println("[POSITION_UPDATE] Position have been update to: X: " + x + ", Y: " + y + ", Z: " + z + ", On Ground: " + onGround);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("[ERROR_WRITING] Error Message: " + ex.getMessage());
        }
    }
}
