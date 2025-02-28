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
        System.out.println(chatMessage);
        /*
        JSONObject jsonObject = new JSONObject(chatMessage);

        String formattedJSON = jsonObject.toString(4);
        if (jsonObject.has("extra")) {
            Object extra = jsonObject.get("extra");
            if (extra instanceof JSONArray) {
                serverConsolePrinter.printChatToConsole((JSONArray) extra);
            }
        } else {
            consolePrinter.ErrorMessage(formattedJSON);
        }

         */
    }
}
