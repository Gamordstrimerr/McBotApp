package me.gamordstrimer.network.config;

import lombok.Getter;

import java.net.Socket;
@Getter
public class StoreSocket {

    private static StoreSocket instance;

    private Socket socket;

    // Private constructor to prevent direct instantiation
    private StoreSocket() {}

    // Get the single instance of BotConfig
    public static StoreSocket getInstance() {
        if (instance == null) {
            instance = new StoreSocket();
        }
        return instance;
    }

    // Method to set socket
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
