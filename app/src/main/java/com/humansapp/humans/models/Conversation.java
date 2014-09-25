package com.humansapp.humans.models;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by jordan on 2014-08-14.
 */
public class Conversation implements Comparable<Conversation> {
    private String id;
    private String[] userIds;
    private String name;
    private String lastMessage;
    private String[] seenIds;
    private String created;
    private String updated;

    public Conversation() {

    }

    public int compareTo(Conversation c) {
        return getUpdated().compareTo(c.getUpdated());
    }

    public String getLastMessage() {
        if(lastMessage == null) {
            return "Be the first to say something!";
        } else {
            return lastMessage;
        }
    }

    public void setLastMessage(Message message) {
        this.lastMessage = message.getBody();
        this.updated = message.getCreatedString();
    }

    public String getName() {
        return this.name;
    }

    public Date getUpdated() {
        DateTime dateTime = new DateTime(updated);
        return dateTime.toDate();
    }

    public String getId() {
        return this.id;
    }

    public String[] getSeenIds() {
        return this.seenIds;
    }

    public void setHasSeen(String[] seenIds) {
        this.seenIds = seenIds;
    }

    public boolean hasSeen(String userId) {
        for(int i = 0; i < seenIds.length; i++) {
            if (seenIds[i].equals(userId)) {
                return true;
            }
        }

        return false;
    }
}
