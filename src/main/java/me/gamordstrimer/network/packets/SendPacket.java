package me.gamordstrimer.network.packets;

import me.gamordstrimer.network.config.StoreSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendPacket {

    private final Socket socket;
    private final OutputStream out;

    public SendPacket() throws IOException {
        this.socket = StoreSocket.getInstance().getSocket();
        if (this.socket == null) {
            throw new IOException("Socket is not initialized in StoreSocket.");
        }
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendPacket(byte[] data) throws IOException {
        PacketWriter.writeVarInt(out, data.length);
        out.write(data);
        out.flush();
    }
}
