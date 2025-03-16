package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.utils.PacketReader;

import java.io.DataInputStream;
import java.io.IOException;

public class DisconnectPacket64 {

    private ConsolePrinter consolePrinter;

    public DisconnectPacket64() {
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void processDisconnection(DataInputStream dataIn) throws IOException {
        String reason = PacketReader.readString(dataIn);

        consolePrinter.ErrorMessage("Disconnected: " + reason);
    }
}
