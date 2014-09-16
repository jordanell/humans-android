package com.humansapp.humans.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
        this.baseURL = "ws://humansapp.com:8080";
    }

    /**
     * Returns a singleton instance of the HumansWebSocketClient
     * @return HumansWebSocketClient.
     */
    public static HumansWebSocketClient instance() {
        if (instance == null) {
            instance = new HumansWebSocketClient();
        }

        return instance();
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

            }

            @Override
            public void onMessage(String s) {

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }
        };

        webSocketClient.connect();
    }
}
