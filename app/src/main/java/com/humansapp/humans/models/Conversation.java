package com.humansapp.humans.models;

import java.util.Map;

/**
 * Created by jordan on 2014-08-14.
 */
public class Conversation extends Model {

    public Conversation(Map model) {
        super(model);
    }

    public String getLastMessage() {
        String message = (String) this.model.get("lastMessage");

        if(message == null) {
            return "Be the first to say something!";
        } else {
            return message;
        }
    }
}
