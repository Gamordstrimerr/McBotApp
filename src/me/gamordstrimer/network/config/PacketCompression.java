package me.gamordstrimer.network.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PacketCompression {

    private static PacketCompression instance;

    private int compression;

    private PacketCompression() {}

    public static PacketCompression getInstance() {
        if (instance == null) {
            instance = new PacketCompression();
        }
        return instance;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }
}
