package me.gamordstrimer.network.config;

import lombok.Getter;

@Getter
public class BotConfig {

    private static BotConfig instance;

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;

    // Private Constructor to prevent direct instantiation
    private BotConfig() {}

    // Get the single instance of BotConfig
    public static BotConfig getInstance() {
        if (instance == null) {
            instance = new BotConfig();
        }
        return instance;
    }

    // Method to set values
    public void setBotConfig(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
    }
}
