package com.humansapp.humans.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.humansapp.humans.R;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;
import com.humansapp.humans.rest.HumansRestClient;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;

/**
 * Created by jordan on 2014-08-22.
 */
public class MessagesAdapter extends ArrayAdapter<Message> {

    ArrayList<Message> messages;
    Context context;

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.list_item_message, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public void add(Message message) {
        super.add(message);
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Message getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        View view;

        if(convertView == null) {
            view = LayoutInflater.from(context).
                    inflate(R.layout.list_item_message, parent, false);
        } else {
            view = convertView;
        }

        boolean myMessage = (HumansRestClient.instance().getUserId().equals(message.getUserId()));

        LinearLayout container = (LinearLayout) view.findViewById(R.id.message_container);
        LinearLayout background = (LinearLayout) view.findViewById(R.id.background_wrapper);

        if(myMessage) {
            container.setGravity(Gravity.RIGHT);
            background.setBackgroundResource(R.drawable.self_sent_bubble);
        }

        TextView tv = (TextView) view.findViewById(R.id.message);
        tv.setText(message.getBody());

        tv = (TextView) view.findViewById(R.id.pretty_date);
        PrettyTime p = new PrettyTime();
        String date = p.format(message.getCreated());
        tv.setText(date);
        if(myMessage) {
            tv.setGravity(Gravity.RIGHT);
        }

        return view;
    }
}
