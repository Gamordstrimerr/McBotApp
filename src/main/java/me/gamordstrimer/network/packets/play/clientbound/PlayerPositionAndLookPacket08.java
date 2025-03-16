package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.network.config.StoreSessionInfos;

import java.io.DataInputStream;
import java.io.IOException;

public class PlayerPositionAndLookPacket08 {

    public void handlePlayerPositionAndLook(DataInputStream dataIn) throws IOException {

        // Read player position and look data
        double x = dataIn.readDouble();
        double y = dataIn.readDouble();
        double z = dataIn.readDouble();

        float yaw = dataIn.readFloat();
        float pitch = dataIn.readFloat();

        byte flags = dataIn.readByte();

        StoreSessionInfos.getInstance().setCurrentX(x);
        StoreSessionInfos.getInstance().setCurrentY(y);
        StoreSessionInfos.getInstance().setCurrentZ(z);
        StoreSessionInfos.getInstance().setCurrentYaw(yaw);
        StoreSessionInfos.getInstance().setCurrentPitch(pitch);
        StoreSessionInfos.getInstance().setCurrentFlags(flags);

        StoreSessionInfos.getInstance().displayCoordinate();
    }
}
