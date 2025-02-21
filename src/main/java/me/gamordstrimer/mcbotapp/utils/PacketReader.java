package me.gamordstrimer.mcbotapp.utils;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketReader {

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

    public static String readString(DataInputStream in) throws IOException {
        int length = readVarInt(in); // Read string length
        byte[] bytes = new byte[length];
        in.readFully(bytes); // Read the UTF-8 encoded string
        return new String(bytes, "UTF-8"); // Convert bytes to string
    }
}
