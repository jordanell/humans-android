package com.humansapp.humans.websocket;

import android.util.Log;

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

    private String baseURL;
    private WebSocketClient webSocketClient;

    /**
     * Instantiates a new HumansWebSocketClient
     */
    protected HumansWebSocketClient() {
        this.baseURL = "ws://192.168.0.104:8080";
    }

    /**
     * Returns a singleton instance of the HumansWebSocketClient
     * @return HumansWebSocketClient.
     */
    public static HumansWebSocketClient instance() {
        if (instance == null) {
            instance = new HumansWebSocketClient();
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
