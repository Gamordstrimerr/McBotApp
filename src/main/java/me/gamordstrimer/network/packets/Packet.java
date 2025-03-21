package me.gamordstrimer.network.packets;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Packet {

    @Getter protected int packetID;
    @Getter protected ConnectionState state;
    protected SendPacket sendPacket;

    public Packet(ConnectionState state) {
        this.state = state;
        try {
            this.sendPacket = new SendPacket();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("[ERROR_BINDING] " + ex.getMessage());
        }
    }

    public abstract @NotNull Integer setPacketID();

    public abstract String setName();

    public abstract void handlePacket(DataInputStream dataIn) throws IOException;

    public boolean isFromState(ConnectionState state) {
        return this.state == state;
    }
}
