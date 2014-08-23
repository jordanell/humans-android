package com.humansapp.humans.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.humansapp.humans.R;

/**
 * Created by jordan on 2014-08-22.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the caret on the action bar
        getActivity().getActionBar().setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Load the preferences
        addPreferencesFromResource(R.layout.fragment_settings);
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
        inflater.inflate(R.menu.fragment_settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
