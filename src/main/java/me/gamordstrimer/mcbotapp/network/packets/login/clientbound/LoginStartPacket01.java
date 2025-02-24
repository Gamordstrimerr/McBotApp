package me.gamordstrimer.mcbotapp.network.packets.login.clientbound;

public class LoginStartPacket01 {

    /*
    private void sendLoginStartPacket(String username) throws IOException {
        buffer.reset(); // Reuse Buffer
        packet = new DataOutputStream(buffer);

        PacketWriter.writeVarInt(packet, 0x00); // Packet ID (Login Start)
        PacketWriter.writeString(packet, username); // Player's Username

        sendPacket.sendPacket(buffer.toByteArray());

        System.out.println("Login Request sent for user: " + username);
    }
    */
}
