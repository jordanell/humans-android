package com.humansapp.humans.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;
import com.humansapp.humans.rest.HumansRestClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jordan on 15/09/14.
 */
public class HumansWebSocketClient {
    private static HumansWebSocketClient instance = null;

    private HumansActivity activity;

    private String baseURL;
    private WebSocketClient webSocketClient;

    /**
     * Instantiates a new HumansWebSocketClient
     */
    protected HumansWebSocketClient(HumansActivity activity) {
        this.baseURL = "ws://192.168.0.104:8080";
        this.activity = activity;
    }

    /**
     * Returns a singleton instance of the HumansWebSocketClient
     * @return HumansWebSocketClient.
     */
    public static HumansWebSocketClient instance(HumansActivity activity) {
        if (instance == null) {
            instance = new HumansWebSocketClient(activity);
        }

        return instance;
    }

    public void connectSocket() {
        URI uri;

        try {
            uri = new URI(this.baseURL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");

                try {
                    JSONObject json = new JSONObject();
                    json.put("userId", HumansRestClient.instance().getUserId());
                    webSocketClient.send(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onMessage(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    Gson gson = new Gson();

                    String type = (String)json.get("type");

                    if (type.equals("message")) {
                        // We have a new message!
                        String data = (String)json.get("data");
                        Message message = gson.fromJson(data, Message.class);
                        activity.getDataStore().addMessage(message.getConversationId(), message);
                    } else if (type.equals("conversation")) {
                        // We have a new conversation!
                        String data = (String)json.get("data");
                        Conversation conversation = gson.fromJson(data, Conversation.class);
                        activity.getDataStore().addConversation(conversation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        webSocketClient.connect();
    }
}
