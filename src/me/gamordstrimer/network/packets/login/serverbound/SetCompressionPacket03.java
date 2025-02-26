package me.gamordstrimer.network.packets.login.serverbound;

import me.gamordstrimer.network.config.PacketCompression;

public class SetCompressionPacket03 {

    public static void setCompression(int compressionThreshold) {
        PacketCompression packetCompression = PacketCompression.getInstance();
        packetCompression.setCompression(compressionThreshold);
        System.out.println("[SET_COMPRESSION] Threshold: " + packetCompression.getCompression());
    }
}
