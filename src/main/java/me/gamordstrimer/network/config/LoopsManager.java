package me.gamordstrimer.network.config;

import lombok.Getter;
import lombok.Setter;
import me.gamordstrimer.network.ResponsesHandler;
import me.gamordstrimer.network.packets.state.ConnectionState;

import java.io.IOException;
import java.net.Socket;

public class LoopsManager {
    @Getter private static final LoopsManager instance = new LoopsManager();

    private Socket socket;

    @Getter private volatile boolean running = true; //Control all the loop
    @Getter @Setter private volatile ConnectionState connectionState = ConnectionState.LOGIN;

    public LoopsManager() {
        this.socket = StoreSocket.getInstance().getSocket();
    }

    public void restartLoop() {
        stop();
        running = true;
        System.out.println("Loops Restarted.");
        connectionState = ConnectionState.LOGIN;
    }

    public void stop() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("[STOPPING] Error:" + ex.getMessage());
        }
    }
}
