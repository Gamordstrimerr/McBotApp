package me.gamordstrimer.network.packets.login.clientbound;

public class HandshakePacket00 {
    /*
    private void sendHandshakePacket() throws IOException {
        buffer.reset(); // Reuse Buffer
        packet = new DataOutputStream(buffer);

        // Packet fields
        int protocolVersion = 47; // Minecraft 1.8.8 protocol version.
        int nextState = 2; //Status request for Login.

        // Write packet data
        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Handshake)
        PacketWriter.writeVarInt(packet, protocolVersion);
        PacketWriter.writeString(packet, SERVER_ADDR);
        packet.writeShort(SERVER_PORTS);
        PacketWriter.writeVarInt(packet, nextState);

        // Converte to byte array and send
        sendPacket.sendPacket(buffer.toByteArray());
        System.out.println("Handshake packet send!");
    }

     */
}
