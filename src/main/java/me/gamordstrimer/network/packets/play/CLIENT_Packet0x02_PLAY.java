package me.gamordstrimer.network.packets.play;

import me.gamordstrimer.network.packets.Packet;
import me.gamordstrimer.network.packets.PacketReader;
import me.gamordstrimer.network.state.ConnectionState;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;

public class CLIENT_Packet0x02_PLAY extends Packet {

    public CLIENT_Packet0x02_PLAY() {
        super(ConnectionState.PLAY);
    }

    @Override
    public Integer setPacketID() {
        return 0x02;
    }

    @Override
    public String setName() {
        return "Receive_Chat_Message_Packet";
    }

    @Override
    public void handlePacket(DataInputStream dataIn) throws IOException {
        String chatMessage = PacketReader.readString(dataIn);
        processIncomingMessages(chatMessage);
    }

    private void processIncomingMessages(String chatMessage) {
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
