package me.gamordstrimer.network.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BotConfig {

    private String SERVER_ADDR;
    private int SERVER_PORTS;
    private String username;
}
