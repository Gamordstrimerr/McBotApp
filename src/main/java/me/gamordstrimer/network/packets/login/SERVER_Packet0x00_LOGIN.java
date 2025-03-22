package me.gamordstrimer.network.packets.login;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SERVER_Packet0x00_LOGIN extends Packet {

    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public SERVER_Packet0x00_LOGIN() {
        super(ConnectionState.LOGIN);

        this.buffer = new ByteArrayOutputStream();
        this.packet = new DataOutputStream(buffer);
    }

    @Override
    public Integer setPacketID() {
        return 0x00;
    }

    @Override
    public String setName() {
        return "Login_Start_Packet";
    }

    public void sendLoginStartPacket(String BOT_USERNAME) throws IOException {
        buffer.reset();
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, packetID);
        PacketWriter.writeString(packet, BOT_USERNAME); // BOT's username

        sendPacket.sendPacket(buffer.toByteArray());

        // System.out.println("Login Request sent for user: " + username);
        consolePrinter.NormalMessage("Login Request sent for user: " + BOT_USERNAME);
    }

}
