package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.config.StoreSessionInfos;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x01_PLAY extends Packet {

    public CLIENT_Packet0x01_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x01;
    }

    @Override
    public String setName() {
        return "Join_Game_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        // Parse the packet fields
        int entityID = dataIn.readInt();
        int gamemode = dataIn.readUnsignedByte();
        byte dimension = dataIn.readByte();
        int difficulty = dataIn.readUnsignedByte();
        int maxPlayers = dataIn.readUnsignedByte();
        String levelType = PacketReader.readString(dataIn);
        boolean reducedDebugInfo = dataIn.readBoolean();

        StoreSessionInfos.getInstance().setEntityID(entityID);
        StoreSessionInfos.getInstance().setGamemode(gamemode);
        StoreSessionInfos.getInstance().setDimension(dimension);
        StoreSessionInfos.getInstance().setDifficulty(difficulty);
        StoreSessionInfos.getInstance().setMaxPlayers(maxPlayers);
        StoreSessionInfos.getInstance().setLevelType(levelType);
        StoreSessionInfos.getInstance().setReducedDebugInfo(reducedDebugInfo);

        StoreSessionInfos.getInstance().displayInfo();
    }
}
