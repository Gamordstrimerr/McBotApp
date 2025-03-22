package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x40_PLAY extends Packet {

    public CLIENT_Packet0x40_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x40;
    }

    @Override
    public String setName() {
        return "Disconnected_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        String reason = PacketReader.readString(dataIn);
        consolePrinter.ErrorMessage("Disconnected: " + reason);
    }
}
