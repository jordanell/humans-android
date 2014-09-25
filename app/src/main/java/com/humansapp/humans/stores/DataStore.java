package com.humansapp.humans.stores;

import android.database.DataSetObserver;

import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.adapters.ConversationsAdapter;
import com.humansapp.humans.adapters.MessagesAdapter;
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

    private HumansActivity activity;

    private ConversationsAdapter conversationsAdapter;
    private HashMap<String, MessagesAdapter> messageAdapters;

    /**
     * Instantiates a new DataStore object.
     */
    public DataStore(HumansActivity activity) {
        this.activity = activity;

        conversationsAdapter = new ConversationsAdapter(activity, new ArrayList<Conversation>());
        messageAdapters = new HashMap<String, MessagesAdapter>();
    }

    /**
     * Returns the current conversations adapter.
     * @return The current conversations adapter.
     */
    public ConversationsAdapter getConversationsAdapter() {
        return conversationsAdapter;
    }

    /**
     * Add a conversation to the conversations list.
     * @param conversation The conversation to add.
     */
    public void addConversation(Conversation conversation) {
        conversationsAdapter.add(conversation);
        conversationsAdapter.sort();
    }

    /**
     * Add conversations to the conversations list.
     * @param conversations The conversations to add.
     */
    public void addConversations(ArrayList<Conversation> conversations) {
        conversationsAdapter.addAll(conversations);
        conversationsAdapter.sort();
    }

    /**
     * Remove a conversation.
     * @param conversationId The id of the conversation to remove.
     */
    public void removeConversation(String conversationId) {
        Conversation c = null;

        for (int i = 0; i < conversationsAdapter.getCount(); i++) {
            if (conversationsAdapter.getItem(i).getId() == conversationId) {
                c = conversationsAdapter.getItem(i);
                break;
            }
        }

        if (c != null) {
            conversationsAdapter.remove(c);
            conversationsAdapter.sort();
        }
    }

    /**
     * Given a conversation id, returns the list of message adapter associated
     * with that conversation.
     * @param conversationId The conversation to find messages of.
     * @return The message adapter.
     */
    public MessagesAdapter getMessageAdapter(final String conversationId) {
        MessagesAdapter adapter = messageAdapters.get(conversationId);

        if (adapter == null) {
            adapter = createMessageAdapter(conversationId);
        }

        return adapter;
    }

    private MessagesAdapter createMessageAdapter(String conversationId) {
        MessagesAdapter adapter = new MessagesAdapter(activity, new ArrayList<Message>());

        final MessagesAdapter ad = adapter;
        final String cId = conversationId;

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                for (int i=-0; i < conversationsAdapter.getCount(); i++) {
                    Conversation c = conversationsAdapter.getItem(i);

                    if (c.getId().equals((cId))) {
                        if (ad.getCount() > 0) {
                            Message lastMessage = ad.getItem(ad.getCount()-1);
                            c.setLastMessage(lastMessage);
                            c.setHasSeen(new String[0]);
                            conversationsAdapter.sort();
                        }
                        break;
                    }
                }
                super.onChanged();
            }
        });
        messageAdapters.put(conversationId, adapter);

        return adapter;
    }

    /**
     * Add a message to a conversation's message adapter.
     * @param conversationId The conversation id to add the message to.
     * @param message The message to add.
     */
    public void addMessage(String conversationId, Message message) {
        MessagesAdapter adapter = messageAdapters.get(conversationId);

        if (adapter == null) {
            adapter = createMessageAdapter(conversationId);
        }

        adapter.add(message);
        adapter.sort();
    }

    /**
     * Add messages to a conversation's message adapter.
     * @param conversationId The conversation id to add the messages to.
     * @param messages The messages to add.
     */
    public void addMessages(String conversationId, ArrayList<Message> messages) {
        MessagesAdapter adapter = messageAdapters.get(conversationId);

        if (adapter == null) {
            adapter = createMessageAdapter(conversationId);
        }

        adapter.addAll(messages);
        adapter.sort();
    }
}
