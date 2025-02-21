package me.gamordstrimer.mcbotapp.network.config;

public class BotConfig {

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;

    public BotConfig(String SERVER_ADDR, int SERVER_PORTS, String username) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORTS = SERVER_PORTS;
        this.username = username;
    }

    public String getSERVER_ADDR() {
        return SERVER_ADDR;
    }

    public void setSERVER_ADDR(String SERVER_ADDR) {
        this.SERVER_ADDR = SERVER_ADDR;
    }

    public int getSERVER_PORTS() {
        return SERVER_PORTS;
    }

    public void setSERVER_PORTS(int SERVER_PORTS) {
        this.SERVER_PORTS = SERVER_PORTS;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
