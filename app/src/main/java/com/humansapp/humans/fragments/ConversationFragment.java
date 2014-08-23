package com.humansapp.humans.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationFragment extends Fragment {
    private String conversationId;
    private String name;

    private View view;

    private RelativeLayout loading;
    private ListView content;
    private TextView empty;
    private RelativeLayout error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        this.view = view;

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
        this.content = (ListView) view.findViewById(R.id.content);
        this.empty = (TextView) view.findViewById(R.id.empty);
        this.error = (RelativeLayout) view.findViewById(R.id.error);

        loadMessages();

        return view;
    }

    private void loadMessages() {
        // Show we are loading something
        loading.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);


        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());
        params.put("conversation_id", this.conversationId);

        HumansRestClient.instance().get("messages", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                showError();
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
                break;
            case R.id.action_leave:
                break;
            case R.id.action_settings:
                SettingsFragment fragment = new SettingsFragment();
                ((HumansActivity)getActivity()).changeFragment(fragment, true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
