package me.gamordstrimer.network.packets.handshake;

import me.gamordstrimer.exception.LoginRequestException;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketWriter;
import me.gamordstrimer.network.packets.login.SERVER_Packet0x00_LOGIN;
import me.gamordstrimer.network.state.ConnectionState;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SERVER_Packet0x00_HANDSHAKE extends Packet {

    private ByteArrayOutputStream buffer;
    private DataOutputStream packet;

    public SERVER_Packet0x00_HANDSHAKE() {
        super(ConnectionState.HANDSHAKE);

        this.buffer = new ByteArrayOutputStream();
        this.packet = new DataOutputStream(buffer);
    }

    @Override
    public Integer setPacketID() {
        return 0x00;
    }

    @Override
    public String setName() {
        return "Handshake_Packet";
    }

    /**
     * Minecraft 1.8.8 (protocol version 47), handshake packet structure :
     *
     * +----------------------------------------------------------------------------------------+
     * |        Field      |         Type       |                     Notes                     |
     * +----------------------------------------------------------------------------------------+
     * | Packet ID         |    VarInt          | Always '0x00' for handshake                   |
     * +----------------------------------------------------------------------------------------+
     * | Protocol version  |    VarInt          | '47' for minecraft 1.8.8                      |
     * +----------------------------------------------------------------------------------------+
     * | Server Address    |    String          | The hostname or IP of the server              |
     * +----------------------------------------------------------------------------------------+
     * | Server Port       |    Unsigned Short  | The port number or the server (e.g., '25565') |
     * +----------------------------------------------------------------------------------------+
     * | Next State        |    VarInt          | '1' for status, '2' for login                 |
     * +----------------------------------------------------------------------------------------+
     **/

    public void sendLoginRequest(String BOT_USERNAME) throws IOException {
        try {
            // Step 1: Send Handshake Packet
            sendHandshakePacket();

            // Step 2: Send Login Start Packet
            new SERVER_Packet0x00_LOGIN().sendLoginStartPacket(BOT_USERNAME);
        } catch (IOException ex) {
            throw new LoginRequestException("Failed to send login request for username: " + BOT_USERNAME, ex);
        }
    }

    private void sendHandshakePacket() throws IOException{
        buffer.reset();
        packet = new DataOutputStream(buffer);

        // Write packet data
        PacketWriter.writeVarInt(packet, packetID);
        PacketWriter.writeVarInt(packet, protocolVersion);
        PacketWriter.writeString(packet, SERVER_ADDR);
        packet.writeShort(SERVER_PORTS);
        PacketWriter.writeVarInt(packet, nextState);

        // Converte to byte array and send
        sendPacket.sendPacket(buffer.toByteArray());

        //System.out.println("Handshake packet send!");
        consolePrinter.NormalMessage("Handshake packet send!");
    }
}
