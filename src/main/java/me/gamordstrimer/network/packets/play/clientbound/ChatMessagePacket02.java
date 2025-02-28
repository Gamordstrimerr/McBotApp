package me.gamordstrimer.network.packets.play.clientbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gamordstrimer.controllers.ServerConsolePrinter;

public class ChatMessagePacket02 {

    private ServerConsolePrinter serverConsolePrinter;

    public ChatMessagePacket02() {
        this.serverConsolePrinter = ServerConsolePrinter.getInstance();
    }

    public void processIncomingMessages(String chatMessage) {

    }


    // Method for debug Message Reception
    public void debugChatMessage(String chatMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the JSON string to a readable (pretty) format
            Object json = objectMapper.readValue(chatMessage, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println("[CHAT_MESSAGE] " + prettyJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
