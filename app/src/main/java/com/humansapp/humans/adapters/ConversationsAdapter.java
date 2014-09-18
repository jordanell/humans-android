package com.humansapp.humans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.humansapp.humans.R;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jordan on 2014-08-14.
 */
public class ConversationsAdapter extends ArrayAdapter<Conversation> {
    Context context;

    public ConversationsAdapter(Context context, ArrayList<Conversation> conversations) {
        super(context, R.layout.list_item_conversation, conversations);
        this.context = context;
    }

    public void sort() {
        super.sort(new Comparator<Conversation>() {
            @Override
            public int compare(Conversation conversation, Conversation conversation2) {
                return conversation.getUpdated().compareTo(conversation2.getUpdated());
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Conversation conversation = getItem(position);
        View view;

        if(convertView == null) {
            view = LayoutInflater.from(context).
                    inflate(R.layout.list_item_conversation, parent, false);
        } else {
            view = convertView;
        }

        TextView tv = (TextView) view.findViewById(R.id.last_message);
        tv.setText(conversation.getLastMessage());

        tv = (TextView) view.findViewById(R.id.conversation_title);
        tv.setText(conversation.getName());

        tv = (TextView) view.findViewById(R.id.pretty_date);
        PrettyTime p = new PrettyTime();
        String date = p.format(conversation.getUpdated());
        tv.setText(date);


        return view;
    }
}
