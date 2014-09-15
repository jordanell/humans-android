package com.humansapp.humans.stores;

import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jordan on 14/09/14.
 */
public class DataStore {

    private ArrayList<Conversation> conversations;
    private HashMap<String, ArrayList<Message>> messages;

    public DataStore() {
        conversations = new ArrayList<Conversation>();
        messages = new HashMap<String, ArrayList<Message>>();
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    public HashMap<String, ArrayList<Message>> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, ArrayList<Message>> messages) {
        this.messages = messages;
    }

    public void addNewMessage(String conversationId, Message message) {
        ArrayList<Message> messageList = messages.get(conversationId);

        if (messageList == null) {
            messageList = new ArrayList<Message>();
        }

        messageList.add(message);
        messages.put(conversationId, messageList);
    }

    public void addOldMessages(String conversationId, ArrayList<Message> messages) {
        ArrayList<Message> messageList = this.messages.get(conversationId);

        if (messageList == null) {
            messageList = new ArrayList<Message>();
        }

        messageList.addAll(messages);
        this.messages.put(conversationId, messageList);
    }

    public void addConversation(Conversation conversation) {
        conversations.add(0, conversation);
    }

    public void addConversations(ArrayList<Conversation> conversations) {
        conversations.addAll(conversations);
    }

    public void removeConversation(Conversation conversation) {
        conversations.remove(conversation);
    }
}
