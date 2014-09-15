package com.humansapp.humans.stores;

import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by jordan on 14/09/14.
 */
public class DataStore {

    private ArrayList<Conversation> conversations;
    private HashMap<String, ArrayList<Message>> messages;

    /**
     * Instantiates a new DataStore object.
     */
    public DataStore() {
        conversations = new ArrayList<Conversation>();
        messages = new HashMap<String, ArrayList<Message>>();
    }

    /**
     * Returns the current list of conversations.
     * @return The current list of conversations.
     */
    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    /**
     * Sets the current list of conversations
     * @param conversations The list of conversations to set.
     */
    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Returns the current hash map of messages.
     * @return The current hash map of messages.
     */
    public HashMap<String, ArrayList<Message>> getMessages() {
        return messages;
    }

    /**
     * Given a conversation id, returns the list of messages associated
     * with that conversation.
     * @param conversationId The conversation to find messages of.
     * @return The list of conversation messages.
     */
    public ArrayList<Message> getMessages(String conversationId) {
        return messages.get(conversationId);
    }

    /**
     * Add a new message to the front of a conversations message list.
     * @param conversationId The conversation id to add the message to.
     * @param message The message to add.
     */
    public void addNewMessage(String conversationId, Message message) {
        ArrayList<Message> messageList = messages.get(conversationId);

        if (messageList == null) {
            messageList = new ArrayList<Message>();
        }

        messageList.add(0, message);
        messages.put(conversationId, messageList);
    }

    /**
     * Add a list of old messages to the end of a conversation's message list.
     * @param conversationId The conversation id to add the messages to.
     * @param messages The messages to add.
     */
    public void addOldMessages(String conversationId, ArrayList<Message> messages) {
        ArrayList<Message> messageList = this.messages.get(conversationId);

        if (messageList == null) {
            messageList = new ArrayList<Message>();
        }

        messageList.addAll(messages);
        this.messages.put(conversationId, messageList);
    }

    /**
     * Add a conversation to the conversations list.
     * @param conversation The conversation to add.
     */
    public void addConversation(Conversation conversation) {
        conversations.add(0, conversation);
    }

    /**
     * Add conversations to the conversations list.
     * @param conversations The conversations to add.
     */
    public void addConversations(ArrayList<Conversation> conversations) {
        this.conversations.addAll(conversations);
        Collections.sort(this.conversations);
    }

    /**
     * Remove a conversation.
     * @param conversation The conversation to remove.
     */
    public void removeConversation(Conversation conversation) {
        conversations.remove(conversation);
    }

    /**
     * Remove a conversation.
     * @param conversationId The id of the conversation to remove.
     */
    public void removeConversation(String conversationId) {
        Conversation c = null;
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getId() == conversationId) {
                c = conversations.get(i);
                break;
            }
        }

        if (c != null) {
            conversations.remove(c);
        }
    }
}
