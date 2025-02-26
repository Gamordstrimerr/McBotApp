package me.gamordstrimer.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendPacket {

    private final OutputStream out;

    public SendPacket(OutputStream out) {
        this.out = out;
    }

    public void sendPacket(byte[] data) throws IOException {
        PacketWriter.writeVarInt(out, data.length);
        out.write(data);
        out.flush();
    }
}
