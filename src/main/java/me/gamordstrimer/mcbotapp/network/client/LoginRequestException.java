package me.gamordstrimer.mcbotapp.network.client;

import java.io.IOException;

public class LoginRequestException extends IOException {

    // Default constructor
    public LoginRequestException() {
        super("An error occured while processing the login request.");
    }

    // Constructor that accepts a custom error message
    public LoginRequestException(String message) {
        super(message);
    }

    // Constructor that accepts both a custom message and a cause (another Throwable)
    public LoginRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts just a cause (another Throwable)
    public LoginRequestException(Throwable cause) {
        super(cause);
    }
}
