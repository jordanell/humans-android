package com.humansapp.humans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.humansapp.humans.R;
import com.humansapp.humans.models.Conversation;
import com.ocpsoft.pretty.time.PrettyTime;

/**
 * Created by jordan on 2014-08-14.
 */
public class ConversationsAdapter extends BaseAdapter {

    Conversation[] conversations;
    Context context;

    public ConversationsAdapter(Context context, Conversation[] conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @Override
    public int getCount() {
        if(conversations == null) {
            return 0;
        } else {
            return conversations.length;
        }
    }

    @Override
    public Conversation getItem(int position) {
        if(conversations == null != position >= 0 && position < conversations.length) {
            return conversations[position];
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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
