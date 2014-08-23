package com.humansapp.humans.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.adapters.MessagesAdapter;
import com.humansapp.humans.models.Message;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationFragment extends Fragment {
    private String conversationId;
    private String name;

    private MessagesAdapter adapter;

    private View view;

    private RelativeLayout loading;
    private ListView list;
    private TextView empty;
    private RelativeLayout error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        this.view = view;

        // Set up the adapter
        adapter = new MessagesAdapter(getActivity(), new ArrayList<Message>());
        addScrollListener();

        // Setup the caret on the action bar
        getActivity().getActionBar().setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup the ID
        String id = getArguments().getString("id", null);
        if(id != null) {
            this.conversationId = id;
        }

        // Setup the name
        String name = getArguments().getString("name", null);
        if(name != null) {
            this.name = name;
            getActivity().getActionBar().setTitle(name);
        }

        // Get view components
        this.loading = (RelativeLayout) view.findViewById(R.id.loading);
        this.list = (ListView) view.findViewById(R.id.list);
        this.empty = (TextView) view.findViewById(R.id.empty);
        this.error = (RelativeLayout) view.findViewById(R.id.error);

        // Set up the auto scrolling
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        // Set the send listener
        Button send = (Button) view.findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        loadMessages();

        return view;
    }

    private void sendMessage() {
        EditText input = (EditText) view.findViewById(R.id.input_message);
        String message = input.getText().toString();

        if(message.isEmpty()) {
            return;
        }

        String encodedMessage;
        try {
            encodedMessage = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Handle this better
            return;

        }

        StringBuilder url = new StringBuilder();
        url.append("messages?user_id=");
        url.append(HumansRestClient.instance().getUserId());
        url.append("&conversation_id=");
        url.append(conversationId);
        url.append("&body=");
        url.append(encodedMessage);

        HumansRestClient.instance().post(url.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                showError();
            }
        });

        input.setText("");
        adapter.add(new Message(HumansRestClient.instance().getUserId(), message));

    }

    private void loadMessages() {
        // Show we are loading something
        loading.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);


        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());
        params.put("conversation_id", conversationId);

        HumansRestClient.instance().get("messages", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonMessages = response.get("messages").toString();
                    Gson gson = new Gson();
                    Message[] messages = gson.fromJson(jsonMessages, Message[].class);

                    MessagesAdapter ad = new MessagesAdapter(getActivity(), new ArrayList<Message>(Arrays.asList(messages)));

                    adapter = ad;
                    list.setAdapter(adapter);
                    addScrollListener();

                    if(ad.getCount() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        list.setVisibility(View.VISIBLE);
                    }

                    loading.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    showError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                showError();
            }
        });
    }

    private void addScrollListener() {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                list.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void showError() {
        empty.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry();
            }
        });
    }

    private void retry() {
        error.setVisibility(View.GONE);

        error.setOnClickListener(null);

        loadMessages();
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().getActionBar().setHomeButtonEnabled(false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setTitle("Humans");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_conversation_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                list.setAdapter(null);
                loadMessages();
                break;
            case R.id.action_leave:
                leavePrompt();
                break;
            case R.id.action_settings:
                SettingsFragment fragment = new SettingsFragment();
                ((HumansActivity)getActivity()).changeFragment(fragment, true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void leavePrompt() {
        new AlertDialog.Builder(getActivity())
            .setTitle("Leave")
            .setMessage("Are you sure you want to leave this human?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    leaveConversation();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    private void leaveConversation() {
        StringBuilder url = new StringBuilder();
        url.append("conversations/leave?user_id=");
        url.append(HumansRestClient.instance().getUserId());
        url.append("&conversation_id=");
        url.append(conversationId);

        HumansRestClient.instance().put(url.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                getActivity().onBackPressed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // Flash something here
            }
        });
    }
}
