package com.humansapp.humans.websocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.media.HumansMedia;
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
                        JSONObject data = json.getJSONObject("data");
                        final Message message = gson.fromJson(data.toString(), Message.class);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.getDataStore().addMessage(message.getConversationId(), message);
                            }
                        });

                        // Vibrate the phone
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                        if (prefs.getBoolean("message_vibrate", true) || HumansMedia.getAudioManager(activity).getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                            HumansMedia.vibrate(activity, 400);
                        }

                        // Play sound
                        String alarm = prefs.getString("message_ringtone", "default ringtone");
                        Uri uri = Uri.parse(alarm);
                        HumansMedia.playAlert(activity, uri);

                    } else if (type.equals("conversation")) {
                        // We have a new conversation!
                        JSONObject data = json.getJSONObject("data");
                        final Conversation conversation = gson.fromJson(data.toString(), Conversation.class);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.getDataStore().addConversation(conversation);
                            }
                        });
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
