package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.config.StoreSessionInfos;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x12_PLAY extends Packet {

    public CLIENT_Packet0x12_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x12;
    }

    @Override
    public String setName() {
        return "Velocity_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        int playerID = StoreSessionInfos.getInstance().getEntityID();
        int entityID = PacketReader.readVarInt(dataIn);

        short velocityX = dataIn.readShort();
        short velocityY = dataIn.readShort();
        short velocityZ = dataIn.readShort();

        double velocityXBlocks = velocityX / 8000.0;
        double velocityYBlocks = velocityY / 8000.0;
        double velocityZBlocks = velocityZ / 8000.0;

        if (entityID == playerID) {
            System.out.println("[ENTITY_VELOCITY] Entity ID: " + entityID
                    + ", Velocity X: " + velocityXBlocks
                    + ", Velocity Y: " + velocityYBlocks
                    + ", Velocity Z: " + velocityZBlocks);

            // Get current position from the class Store Session Infos
            double currentX = StoreSessionInfos.getInstance().getCurrentX();
            double currentY = StoreSessionInfos.getInstance().getCurrentY();
            double currentZ = StoreSessionInfos.getInstance().getCurrentZ();

            // Calculate new position after movement
            double newX = currentX + velocityXBlocks;
            double newY = currentY + velocityYBlocks;
            double newZ = currentZ + velocityZBlocks;

            // Assume player is on ground if velocityY is low
            boolean onGround = velocityYBlocks < 0.01;

            // Update stored player position
            StoreSessionInfos.getInstance().updatePosition(newX, newY, newZ);
        }
    }
}
