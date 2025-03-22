package me.gamordstrimer.network.packets.login;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.Console;
import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x00_LOGIN extends Packet {

    public CLIENT_Packet0x00_LOGIN() {
        super(ConnectionState.LOGIN);
    }

    @Override
    public Integer setPacketID() {
        return 0x00;
    }

    @Override
    public String setName() {
        return "Disconnected_Login_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        String disconnectedMessage = PacketReader.readString(dataIn);
        consolePrinter.ErrorMessage("[DISCONNECTED] Message: " + disconnectedMessage);
    }
}
