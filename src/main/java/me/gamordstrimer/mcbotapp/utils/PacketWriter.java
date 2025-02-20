package me.gamordstrimer.mcbotapp.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketWriter {

    public static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    public static void writeVarInt(OutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    public static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] stringBytes = value.getBytes("UTF-8");
        writeVarInt(out, stringBytes.length); // Write length as VarInt
        out.write(stringBytes); // Write actual string bytes
    }
}
