package com.humansapp.humans.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.humansapp.humans.R;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.Message;
import com.humansapp.humans.rest.HumansRestClient;
import com.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

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
                return conversation2.getUpdated().compareTo(conversation.getUpdated());
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

        boolean hasSeen = conversation.hasSeen(HumansRestClient.instance().getUserId());

        TextView tv = (TextView) view.findViewById(R.id.last_message);
        tv.setText(conversation.getLastMessage());
        if (!hasSeen)
            tv.setTypeface(null, Typeface.BOLD);

        tv = (TextView) view.findViewById(R.id.conversation_title);
        tv.setText(conversation.getName(HumansRestClient.instance().getUserId()));
        if (!hasSeen)
            tv.setTypeface(null, Typeface.BOLD);

        tv = (TextView) view.findViewById(R.id.pretty_date);
        PrettyTime p = new PrettyTime();
        String date = p.format(conversation.getUpdated());
        tv.setText(date);
        if (!hasSeen)
            tv.setTypeface(null, Typeface.BOLD);

        int[] images = {R.drawable.human1, R.drawable.human2, R.drawable.human3};
        ImageView imageView = (ImageView) view.findViewById(R.id.conversation_icon);
        Random rn = new Random();
        int random = rn.nextInt((images.length-1) - 0 + 1) + 0;
        imageView.setBackgroundResource(images[random]);

        return view;
    }
}
