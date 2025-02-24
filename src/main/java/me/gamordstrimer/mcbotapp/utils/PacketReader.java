package me.gamordstrimer.mcbotapp.utils;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.zip.InflaterInputStream;

public class PacketReader {

    @Getter
    private static int lastReadVarIntSize = 0; // Stores the last VarInt size

    public static String readString(DataInputStream in) throws IOException {
        int length = readVarInt(in); // Read string length
        byte[] bytes = new byte[length];
        in.readFully(bytes); // Read the UTF-8 encoded string
        return new String(bytes, "UTF-8"); // Convert bytes to string
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = in.readByte();
            int value = (read & 0b01111111); // Remove the MSB
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new IOException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0); // Continue while MSB is 1

        lastReadVarIntSize = numRead; // Store the number of bytes read
        return result;
    }

    public static byte[] readCompressedPacket(DataInputStream in, int packetLength, int compressionThreshold) throws IOException {
        if (compressionThreshold < 0) {
            throw new IllegalStateException("Compression threshold must be >= 0");
        }

        // Read uncompressed size
        int uncompressedSize = readVarInt(in);

        // If the size is 0, the packet is uncompressed
        if (uncompressedSize == 0) {
            byte[] data = new byte[packetLength - getLastReadVarIntSize()];
            in.readFully(data);
            return data;
        }

        // Otherwise, decompress the packet
        byte[] compressedData = new byte[packetLength - getLastReadVarIntSize()];
        in.readFully(compressedData);

        InflaterInputStream inflaterStream = new InflaterInputStream(new ByteArrayInputStream(compressedData));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inflaterStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        return outputStream.toByteArray();
    }
}

