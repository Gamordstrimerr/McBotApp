package me.gamordstrimer.network.packets.login.clientbound;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.network.config.PacketCompression;

public class SetCompressionPacket03 {

    public static void setCompression(int compressionThreshold) {
        PacketCompression packetCompression = PacketCompression.getInstance();
        packetCompression.setCompression(compressionThreshold);
        ConsolePrinter consolePrinter = ConsolePrinter.getInstance();
        consolePrinter.NormalMessage("[SET_COMPRESSION] Threshold: " + packetCompression.getCompression());
    }
}
