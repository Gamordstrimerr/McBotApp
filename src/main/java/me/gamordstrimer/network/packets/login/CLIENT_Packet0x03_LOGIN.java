package me.gamordstrimer.network.packets.login;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.PacketCompression;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x03_LOGIN extends Packet {

    public CLIENT_Packet0x03_LOGIN() {
        super(ConnectionState.LOGIN);
    }

    @Override
    public Integer setPacketID() {
        return 0x03;
    }

    @Override
    public String setName() {
        return "Set_Compression_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        int compressionThreshold = PacketReader.readVarInt(dataIn);
        setCompression(compressionThreshold);
    }

    private void setCompression(int compressionThreshold) {
        PacketCompression packetCompression = PacketCompression.getInstance();
        packetCompression.setCompression(compressionThreshold);
        consolePrinter.NormalMessage("[SET_COMPRESSION] Threshold: " + packetCompression.getCompression());
    }
}
