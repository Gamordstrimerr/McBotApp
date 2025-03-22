package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.config.LoopsManager;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x08_PLAY extends Packet implements Runnable {
    private final LoopsManager loopsManager = LoopsManager.getInstance();

    private double x;  // X position of the bot
    private double y;  // Y position of the bot
    private double z;  // Z position of the bot

    private double velocityY = 0.0; // Y velocity of the bot (needed for proper gravity)
    private final double gravity = 0.08; // Gravity acceleration per tick
    private final double maxFallSpeed = -3.92; // Terminal velocity (approx. from Minecraft)
    private double groundLevel = 62; // Default ground level (should be detected dynamically)
    private boolean onGround = false; // Whether the bot is on the ground or not

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
        Thread movementThread = new Thread(this);
        movementThread.start();
    }

    @Override
    public void run() {
        while (!onGround || loopsManager.isRunning()) {
            updateBotPosition();
            try {
                Thread.sleep(50); // Simulate 20 TPS (Minecraft runs at 20 ticks per second)
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Update the bot's position and simulate gravity (called every tick)
    public void updateBotPosition() {
        checkGroundContact();  // Check if the bot is on the ground
        applyGravity();        // Apply gravity

        sendPositionUpdate(); // Send the position update to the server
    }

    private void applyGravity() {
        if (!onGround) {
            // Increase velocity due to gravity (simulate falling acceleration)
            velocityY -= gravity;

            // Clamp velocity to terminal velocity
            if (velocityY < maxFallSpeed) {
                velocityY = maxFallSpeed;
            }

            // Apply velocity to position
            y += velocityY;
        } else {
            velocityY = 0; // Reset velocity when on the ground
        }
    }

    // Method to dynamically check the ground level
    private void checkGroundContact() {
        double detectedGroundLevel = getGroundLevel(x, y, z);

        if (y <= detectedGroundLevel) {
            y = detectedGroundLevel; // Snap to the ground
            onGround = true;
        } else {
            onGround = false;
        }
    }

    // Simulated method to get the actual ground level (you need to replace this with real-world detection)
    private double getGroundLevel(double x, double y, double z) {
        // In reality, you should check the world data to determine the actual block at (x, z).
        // For now, assume the ground is at y = 62 (modify as needed).
        return 62;
    }

    private void sendPositionUpdate() {
        new SERVER_Packet0x04_PLAY().sendPositionUpdate(x, y, z, onGround);
    }
}
