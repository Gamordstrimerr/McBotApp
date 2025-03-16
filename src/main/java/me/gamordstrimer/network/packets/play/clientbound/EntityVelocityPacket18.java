package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.network.config.StoreSessionInfos;
import me.gamordstrimer.network.packets.play.serverbound.PlayerPositionPacket04;
import me.gamordstrimer.utils.PacketReader;
import me.gamordstrimer.utils.SendPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EntityVelocityPacket18 {

    private SendPacket sendPacket;
    private ByteArrayOutputStream buffer;

    public EntityVelocityPacket18(SendPacket sendPacket) {
        this.sendPacket = sendPacket;
        this.buffer = new ByteArrayOutputStream();
    }

    public void handlePlayerVelocity(DataInputStream dataIn) throws IOException {
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

            // Calculate new position after knockback
            double newX = currentX + velocityXBlocks;
            double newY = currentY + velocityYBlocks;
            double newZ = currentZ + velocityZBlocks;

            // Assume player is on ground if velocityY is low
            boolean onGround = velocityYBlocks < 0.01;

            // Send the updated position packet
            PlayerPositionPacket04 positionPacket = new PlayerPositionPacket04(newX, newY, newZ, onGround, sendPacket);
            positionPacket.processPlayerPosition();

            // Update stored player position
            StoreSessionInfos.getInstance().setCurrentX(newX);
            StoreSessionInfos.getInstance().setCurrentY(newY);
            StoreSessionInfos.getInstance().setCurrentZ(newZ);
        }
    }
}
