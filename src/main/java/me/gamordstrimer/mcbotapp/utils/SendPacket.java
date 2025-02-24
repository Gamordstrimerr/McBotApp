package me.gamordstrimer.mcbotapp.utils;

import me.gamordstrimer.mcbotapp.network.config.PacketCompression;

import java.io.IOException;
import java.io.OutputStream;

public class SendPacket {
    private OutputStream out;
    private PacketCompression packetCompression;

    public SendPacket(OutputStream out) {
        this.out = out;
    }

    public void sendPacket(byte[] data) throws IOException {
        PacketWriter.writeVarInt(out, data.length);
        out.write(data);
        out.flush();
    }
}
