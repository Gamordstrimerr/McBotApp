package me.gamordstrimer.network.config;

import lombok.Getter;

@Getter
public class ConnectionConfig {

    private static ConnectionConfig instance;

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;

    // Private Constructor to prevent direct instantiation
    private ConnectionConfig() {}

    // Get the single instance of BotConfig
    public static ConnectionConfig getInstance() {
        if (instance == null) {
            instance = new ConnectionConfig();
        }
        return instance;
    }

    // Method to set values
    public void setConnectionConfig(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
    }
}
