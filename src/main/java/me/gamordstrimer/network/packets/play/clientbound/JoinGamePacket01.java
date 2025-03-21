package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.network.config.StoreSessionInfos;
import me.gamordstrimer.network.packets.PacketReader;

import java.io.DataInputStream;
import java.io.IOException;

public class JoinGamePacket01 {

    public void processJoinGamePacket(DataInputStream dataIn) throws IOException {
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
