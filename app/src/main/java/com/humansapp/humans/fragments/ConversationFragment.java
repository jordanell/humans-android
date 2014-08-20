package com.humansapp.humans.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.humansapp.humans.R;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationFragment extends Fragment {
    private String conversationId;
    private String name;

    private View view;

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

        return view;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
