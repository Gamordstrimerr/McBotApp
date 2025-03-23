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

    private final Map<Long, byte[]> chunkDataMap = new HashMap<>();

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
        int dataSize = dataIn.readInt(); // Size of compressed chunk data

        byte[] compressedData = new byte[dataSize];
        dataIn.readFully(compressedData); // Read compressed chunk data

        // Decompress the data
        byte[] decompressedData = decompressChunkData(compressedData);

        // Store chunk data
        long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
        chunkDataMap.put(chunkKey, decompressedData);

        System.out.println("[CHUNK LOADED] (" + chunkX + ", " + chunkZ + ")");
    }

    public static byte[] decompressChunkData(byte[] compressedData) throws IOException {
        Inflater inflater = new Inflater(); // Zlib decompression
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
             InflaterInputStream inflaterStream = new InflaterInputStream(byteStream, inflater);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096]; // 4KB buffer
            int bytesRead;
            while ((bytesRead = inflaterStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray(); // Return decompressed chunk data
        } finally {
            inflater.end(); // Clean up resources
        }
    }
}
