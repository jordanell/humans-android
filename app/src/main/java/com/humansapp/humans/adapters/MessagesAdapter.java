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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by jordan on 2014-08-22.
 */
public class MessagesAdapter extends ArrayAdapter<Message> {
    Context context;

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.list_item_message, messages);
        this.context = context;
    }

    public void sort() {
        super.sort(new Comparator<Message>() {
            @Override
            public int compare(Message message, Message message2) {
                return message.getCreated().compareTo(message2.getCreated());
            }
        });
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

        // Is it a system message?
        boolean systemMessage = (message.getUserId() == null);
        if (systemMessage) {
            LinearLayout systemWrapper = (LinearLayout) view.findViewById(R.id.system_message);
            systemWrapper.setVisibility(View.VISIBLE);

            TextView tv = (TextView) view.findViewById(R.id.smessage);
            tv.setText(message.getBody());

            LinearLayout background = (LinearLayout) view.findViewById(R.id.background_wrapper);
            background.setVisibility(View.GONE);

            return view;
        }

        // Deal with a user message
        boolean myMessage = (HumansRestClient.instance().getUserId().equals(message.getUserId()));

        LinearLayout container = (LinearLayout) view.findViewById(R.id.message_container);
        LinearLayout background = (LinearLayout) view.findViewById(R.id.background_wrapper);

        if(myMessage) {
            container.setGravity(Gravity.RIGHT);
            background.setBackgroundResource(R.drawable.self_sent_bubble);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(250, 0, 0, 0);
            background.setLayoutParams(params);
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
