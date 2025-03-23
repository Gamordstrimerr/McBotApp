package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x21_PLAY extends Packet {

    private int chunkX;
    private int chunkZ;

    public CLIENT_Packet0x21_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x21;
    }

    @Override
    public String setName() {
        return "Chunk_Data_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {

        // Skip the chunk coordinates if you don't need them
        dataIn.readInt();  // Skip chunkX
        dataIn.readInt();  // Skip chunkZ

        System.out.println("[CHUNK LOADED] (" + chunkX + ", " + chunkZ + ")");
    }

    public void setChunkCoordinate(double playerX, double playerZ) {
        chunkX = (int) Math.floor(playerX / 16);
        chunkZ = (int) Math.floor(playerZ / 16);

        // System.out.println("[CHUNK LOADED] (" + chunkX + ", " + chunkZ + ")");
    }
}
