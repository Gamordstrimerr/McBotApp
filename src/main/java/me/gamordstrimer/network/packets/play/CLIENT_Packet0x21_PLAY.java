package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class CLIENT_Packet0x21_PLAY extends Packet {

    private static final Map<Long, byte[]> chunkDataMap = new HashMap<>(); // Make it static for global access
    private static final Map<Long, Integer> bitMaskMap = new HashMap<>(); // Store bitmask for each chunk

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
        int primaryBitMask = dataIn.readUnsignedShort();
        int dataSize = dataIn.readInt();

        byte[] compressedData = new byte[dataSize];
        dataIn.readFully(compressedData);

        byte[] decompressedData = decompressChunkData(compressedData);
        long chunkKey = getChunkKey(chunkX, chunkZ);

        // Store the chunk data
        chunkDataMap.put(chunkKey, decompressedData);
        bitMaskMap.put(chunkKey, primaryBitMask);

        System.out.println("[CHUNK LOADED] (" + chunkX + ", " + chunkZ + ")");
    }

    public static byte[] decompressChunkData(byte[] compressedData) throws IOException {
        Inflater inflater = new Inflater();
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
             InflaterInputStream inflaterStream = new InflaterInputStream(byteStream, inflater);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096]; // 4KB buffer
            int bytesRead;
            while ((bytesRead = inflaterStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } finally {
            inflater.end();
        }
    }

    /**
     * Fetches the ground level for the given (X, Z).
     */
    public static int getGroundLevel(int blockX, int blockZ) {
        int chunkX = blockX >> 4;
        int chunkZ = blockZ >> 4;
        long chunkKey = getChunkKey(chunkX, chunkZ);

        if (!chunkDataMap.containsKey(chunkKey)) {
            return -1; // Chunk not loaded
        }

        byte[] chunkData = chunkDataMap.get(chunkKey);
        int bitMask = bitMaskMap.get(chunkKey);

        return findGroundLevel(chunkData, bitMask, blockX, blockZ);
    }

    private static long getChunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    private static int findGroundLevel(byte[] decompressedData, int primaryBitMask, int blockX, int blockZ) {
        final int CHUNK_SECTION_HEIGHT = 16;
        final int CHUNK_WIDTH = 16;

        int localX = blockX & 15;
        int localZ = blockZ & 15;
        int offset = 0;

        for (int section = 15; section >= 0; section--) {
            if ((primaryBitMask & (1 << section)) == 0) continue;

            for (int y = CHUNK_SECTION_HEIGHT - 1; y >= 0; y--) {
                int globalY = (section * CHUNK_SECTION_HEIGHT) + y;
                int index = (y * CHUNK_WIDTH * CHUNK_WIDTH) + (localZ * CHUNK_WIDTH) + localX;
                int blockState = ((decompressedData[offset + (index * 2)] & 0xFF) << 8)
                        | (decompressedData[offset + (index * 2) + 1] & 0xFF);

                if (blockState != 0) {
                    return globalY;
                }
            }
            offset += (CHUNK_SECTION_HEIGHT * CHUNK_WIDTH * CHUNK_WIDTH * 2);
        }

        return -1;
    }
}
