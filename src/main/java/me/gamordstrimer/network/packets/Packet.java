package me.gamordstrimer.network.packets;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.controllers.ServerConsolePrinter;
import me.gamordstrimer.network.config.ConnectionConfig;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {

    @Getter protected int packetID;
    @Getter protected ConnectionState state;
    protected SendPacket sendPacket;
    protected ConsolePrinter consolePrinter;
    protected ServerConsolePrinter serverConsolePrinter;

    // Packet Fields
    protected int protocolVersion = 47; // Minecraft 1.8.X protocol Version.
    protected int nextState = 2; // Status for login Request.

    // Server Infos
    protected String SERVER_ADDR = ConnectionConfig.getInstance().getSERVER_ADDR();
    protected int SERVER_PORTS = ConnectionConfig.getInstance().getSERVER_PORTS();

    public Packet(ConnectionState state) {
        this.state = state;
        this.packetID = setPacketID(); // Ensure packet ID is set during instantiation
        this.consolePrinter = ConsolePrinter.getInstance(); // instensify Console Printer
        this.serverConsolePrinter = ServerConsolePrinter.getInstance(); // instensify Server Console Printer

        try {
            this.sendPacket = new SendPacket();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("[ERROR_BINDING] " + ex.getMessage());
        }
    }

    public abstract @NotNull Integer setPacketID();

    public abstract String setName();

    // Default implementation (empty)
    public void handlePacket(DataInputStream dataIn) throws IOException {
        // No operation, empty implementation
    }

    public boolean isFromState(ConnectionState state) {
        return this.state == state;
    }
}
