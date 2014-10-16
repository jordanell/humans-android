package com.humansapp.humans.models;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jordan on 2014-08-14.
 */
public class Conversation implements Comparable<Conversation> {
    private String id;
    private ArrayList<User> users;
    private Message lastMessage;
    private ArrayList<User> seenUsers;
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
            return lastMessage.getBody();
        }
    }

    public void setLastMessage(Message message) {
        this.lastMessage = message;
        this.updated = message.getCreatedString();
    }

    public String getName(String id) {
        for(User user: users) {
            if (!user.getId().equals(id)) {
                return user.getName();
            }
        }
        return "Anonymous";
    }

    public Date getUpdated() {
        DateTime dateTime = new DateTime(updated);
        return dateTime.toDate();
    }

    public String getId() {
        return this.id;
    }

    public ArrayList<User> getSeenUsers() {
        return this.seenUsers;
    }

    public void setHasSeen(ArrayList<User> seenUsers) {
        this.seenUsers = seenUsers;
    }

    public boolean hasSeen(String userId) {
        for(User user: seenUsers) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
