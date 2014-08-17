package com.humansapp.humans.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

/**
 * Created by jordan on 2014-08-14.
 */
public class Conversation extends Model {
    private String id;
    private String[] userIds;
    private String lastMessage;
    private String created;
    private String updated;

    public Conversation() {

    }

    public String getLastMessage() {
        if(lastMessage == null) {
            return "Be the first to say something!";
        } else {
            return lastMessage;
        }
    }

    public DateTime getUpdated() {
        DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
        return parser2.parseDateTime(updated);
    }
}
