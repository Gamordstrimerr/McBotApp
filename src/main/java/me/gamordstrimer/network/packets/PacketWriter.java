package me.gamordstrimer.network.packets;

import me.gamordstrimer.network.config.PacketCompression;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

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

    // Compress a byte array using Zlib (Deflater)
    public static byte[] compress(byte[] data) throws IOException {
        int compressionThreshold = PacketCompression.getInstance().getCompression();
        if (data == null || data.length == 0) {
            return new byte[]{0}; // Return a single "0" byte if there's nothing to compress
        }
        if (data.length < compressionThreshold) {
            // No compression needed, prefix with uncompressed length as a VarInt
            ByteArrayOutputStream uncompressed = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(uncompressed);
            writeVarInt(out, 0); // Indicate uncompressed data
            out.write(data);
            return uncompressed.toByteArray();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteArrayOutputStream);

        // Write uncompressed length first
        writeVarInt(dataOut, data.length);

        // Compress the data
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
        deflaterOutputStream.write(data);
        deflaterOutputStream.close(); // Finish compression

        return byteArrayOutputStream.toByteArray();
    }
}
