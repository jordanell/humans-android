package com.humansapp.humans.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humansapp.humans.R;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        this.view = view;

        // Setup the caret on the action bar
        getActivity().getActionBar().setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().getActionBar().setHomeButtonEnabled(false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
