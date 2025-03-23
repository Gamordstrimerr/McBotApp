package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.config.LoopsManager;
import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x08_PLAY extends Packet implements Runnable {
    private final LoopsManager loopsManager = LoopsManager.getInstance();

    private double x, y, z;
    private double vx = 0, vy = 0, vz = 0;
    private final double gravity = 0.08;
    private final double airDrag = 0.02;
    private final double groundDrag = 0.1;
    private final double maxFallSpeed = -3.92;
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

    private void updateBotPosition() {
        checkGroundContact();
        applyPhysics();
        sendPositionUpdate();
    }

    private void applyPhysics() {
        if (!onGround) {
            vy -= gravity;
            vy = Math.max(vy, maxFallSpeed);
        } else {
            vy = 0;
            vx *= (1.0 - groundDrag);
            vz *= (1.0 - groundDrag);
        }

        vx *= (1.0 - airDrag);
        vz *= (1.0 - airDrag);

        x += vx;
        y += vy;
        z += vz;
    }

    private void checkGroundContact() {
        int detectedGroundLevel = CLIENT_Packet0x21_PLAY.getGroundLevel((int) x, (int) z);

        if (detectedGroundLevel != -1 && y <= detectedGroundLevel) {
            y = detectedGroundLevel;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }

    private void sendPositionUpdate() {
        new SERVER_Packet0x04_PLAY().sendPositionUpdate(x, y, z, onGround);
    }
}
