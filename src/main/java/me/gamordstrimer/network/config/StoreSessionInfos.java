package me.gamordstrimer.network.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StoreSessionInfos {

    private static StoreSessionInfos instance;

    private int entityID;
    private int gamemode;
    private byte dimension;
    private int difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reducedDebugInfo;

    // Coordinate
    private double currentX;
    private double currentY;
    private double currentZ;
    private float currentYaw;
    private float currentPitch;
    private byte currentFlags;

    private StoreSessionInfos() {}

    public static StoreSessionInfos getInstance() {
        if (instance == null) {
            instance = new StoreSessionInfos();
        }
        return instance;
    }

    public void displayInfo() {
        System.out.println("=== Parsed Join Game Packet ===");
        System.out.println("Entity ID: " + entityID);
        System.out.println("Gamemode: " + gamemode);
        System.out.println("Dimension: " + dimension);
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Max Players: " + maxPlayers);
        System.out.println("Level Type: " + levelType);
        System.out.println("Reduced Debug Info: " + reducedDebugInfo);
    }

    public void displayCoordinate() {
        System.out.println("=== Bot Current Coordinate ===");
        System.out.println("Current X: " + currentX);
        System.out.println("Current Y: " + currentY);
        System.out.println("Current Z: " + currentZ);
        System.out.println("Yaw: " + currentYaw);
        System.out.println("Pitch: " + currentPitch);
        System.out.println("Flags: " + currentFlags);
    }

    public void updatePosition(double x, double y, double z) {
        this.currentX = x;
        this.currentY = y;
        this.currentZ = z;
        System.out.println("[POSITION_UPDATE] X: " + x + ", Y: " + y + ", Z: " + z);
    }
}
