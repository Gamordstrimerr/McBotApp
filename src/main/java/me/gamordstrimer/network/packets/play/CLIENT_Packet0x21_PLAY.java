package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
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
        int chunkX = dataIn.readInt();
        int chunkZ = dataIn.readInt();
        boolean groundUpContinuous = dataIn.readBoolean();
        int primaryBitMask = dataIn.readUnsignedShort(); // Which sections are included
        int dataSize = PacketReader.readVarInt(dataIn); // Size of compressed chunk data

        byte[] compressedData = new byte[dataSize];
        dataIn.readFully(compressedData);
    }

    public double getGroundLevelFromChunk(byte[] decompressedChunkData, double chunkX, double chunkZ) {
        double y = 0;

        // Process the decompressed chunk data and find the heightmap or the highest block at the X, Z position
        // Use Minecraft's chunk format to parse the chunk and find the height (you'll need to decode the chunk data format here)

        // Simulated logic: Iterate through chunk sections and check for the highest non-air block at the given X, Z
        // You can also access the heightmap for faster lookup of the terrain height

        // This is just a placeholder; you need to decode the chunk data to extract the correct Y-level.
        y = 62;  // Placeholder Y-level, replace with actual logic to find the ground height

        return y;
    }
}
