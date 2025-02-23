package me.gamordstrimer.mcbotapp.utils;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketReader {

    @Getter
    private static int lastReadVarIntSize = 0; // Stores the last VarInt size

    /*
    public static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        do {
            currentByte = in.readByte();
            value |= (currentByte & 0x7F) << (position++ * 7);

            if (position > 5) {
                throw new IOException("VarInt too big");
            }
        } while ((currentByte & 0x80) != 0);

        return value;
    }
     */

    public static String readString(DataInputStream in) throws IOException {
        int length = readVarInt(in); // Read string length
        byte[] bytes = new byte[length];
        in.readFully(bytes); // Read the UTF-8 encoded string
        return new String(bytes, "UTF-8"); // Convert bytes to string
    }

    public static UUID readUUID(DataInputStream in) throws IOException {
        long mostSigBits = in.readLong();  // Read first 8 bytes (most significant bits)
        long leastSigBits = in.readLong(); // Read next 8 bytes (least significant bits)
        return new UUID(mostSigBits, leastSigBits);
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
}
