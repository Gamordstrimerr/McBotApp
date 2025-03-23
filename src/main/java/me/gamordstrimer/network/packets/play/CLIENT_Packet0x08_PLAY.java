package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.config.LoopsManager;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x08_PLAY extends Packet implements Runnable {
    private final LoopsManager loopsManager = LoopsManager.getInstance();

    private double x,y,z;  // Position
    private double vx = 0,vy = 0,vz = 0; // Velocity
    private final double gravity = 0.08; // Gravity constant
    private final double airDrag = 0.02; // Air drag factor
    private final double groundDrag = 0.1; // Ground friction
    private final double maxFallSpeed = -3.92; // Terminal velocity
    private boolean onGround = false;

    public CLIENT_Packet0x08_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x08;
    }

    @Override
    public String setName() {
        return "Player_Position_And_Look_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        x = dataIn.readDouble();
        y = dataIn.readDouble();
        z = dataIn.readDouble();

        // Start movement logic in a new thread
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!onGround && loopsManager.isRunning()) { // Stop when onGround is true
            updateBotPosition();
            try {
                Thread.sleep(50); // 20 TPS
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("[STOPPED] Bot reached the ground. Exiting loop.");
    }

    // Update the bot's position and simulate gravity (called every tick)
    private void updateBotPosition() {
        checkGroundContact();
        applyPhysics();
        sendPositionUpdate();
    }

    private void applyPhysics() {
        if (!onGround) {
            vy -= gravity; // Apply gravity
            vy = Math.max(vy, maxFallSpeed); // Cap falling speed
        } else {
            vy = 0; // Reset velocity when on ground
            vx *= (1.0 - groundDrag); // Apply ground friction
            vz *= (1.0 - groundDrag);
        }

        // Apply drag
        vx *= (1.0 - airDrag);
        vz *= (1.0 - airDrag);

        // Update position
        x += vx;
        y += vy;
        z += vz;
    }

    // Method to dynamically check the ground level
    private void checkGroundContact() {
        double detectedGroundLevel = getGroundLevel(x, y, z);

        if (y <= detectedGroundLevel) {
            y = detectedGroundLevel;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }

    // Simulated method to get the actual ground level (you need to replace this with real-world detection)
    private double getGroundLevel(double x, double y, double z) {
        return 62; // Placeholder, replace with world data check
    }

    private void sendPositionUpdate() {
        new SERVER_Packet0x04_PLAY().sendPositionUpdate(x, y, z, onGround);
        new CLIENT_Packet0x21_PLAY().setChunkCoordinate(x, z);
    }
}
