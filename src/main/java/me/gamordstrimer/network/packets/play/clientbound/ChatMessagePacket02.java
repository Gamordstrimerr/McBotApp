package me.gamordstrimer.network.packets.play.clientbound;

import me.gamordstrimer.controllers.ConsolePrinter;
import me.gamordstrimer.controllers.ServerConsolePrinter;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatMessagePacket02 {

    private ServerConsolePrinter serverConsolePrinter;
    private ConsolePrinter consolePrinter;

    public ChatMessagePacket02() {
        this.serverConsolePrinter = ServerConsolePrinter.getInstance();
        this.consolePrinter = ConsolePrinter.getInstance();
    }

    public void processIncomingMessages(String chatMessage) {
        if (isPotentialJson(chatMessage)) {
            try {
                JSONObject jsonObject = new JSONObject(chatMessage);

                String formattedJSON = jsonObject.toString(4);
                if (jsonObject.has("extra")) {
                    Object extra = jsonObject.get("extra");
                    if (extra instanceof JSONArray) {
                        serverConsolePrinter.printJSONChatToConsole((JSONArray) extra);
                    }
                } else {
                    consolePrinter.ErrorMessage(formattedJSON);
                }
            } catch (Exception ex) {
                consolePrinter.ErrorMessage("Failed to parse JSON: " + ex.getMessage());
            }
        } else {
            serverConsolePrinter.printSTRINGChatToConsole(chatMessage);
        }
    }

    // Check if the String is a potential JSON message
    private boolean isPotentialJson(String message) {
        message = message.trim();
        return message.startsWith("{") && message.endsWith("}");
    }
}
