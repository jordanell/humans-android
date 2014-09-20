package com.humansapp.humans.models;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    public Message(String conversationId, String userId, String body) {
        this.userId = userId;
        this.body = body;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        this.created = df.format(new Date());
    }

    public int compareTo(Message m) {
        return getCreated().compareTo(m.getCreated());
    }

    public Date getCreated() {
        DateTime dateTime = new DateTime(created);
        return dateTime.toDate();
    }

    public String getCreatedString() {
        return this.created;
    }

    public String getName() {
        return this.name;
    }

    public String getBody() { return this.body; }

    public String getUserId() { return this.userId; }

    public String getId() { return this.id; }

    public String getConversationId() { return this.conversationId; }
}
