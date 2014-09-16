package com.humansapp.humans.models;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by jordan on 2014-08-22.
 */
public class Message implements Comparable<Message> {
    private String id;
    private String userId;
    private String name;
    private String body;
    private String conversationId;
    private String created;

    public Message() {

    }

    public int compareTo(Message m) {
        return getCreated().compareTo(m.getCreated());
    }

    public Message(String userId, String body) {
        this.userId = userId;
        this.body = body;
    }

    public Date getCreated() {
        DateTime dateTime = new DateTime(created);
        return dateTime.toDate();
    }

    public String getName() {
        return this.name;
    }

    public String getBody() { return this.body; }

    public String getUserId() { return this.userId; }

    public String getId() { return this.id; }

    public String getConversationId() { return this.conversationId; }
}
