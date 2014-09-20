package com.humansapp.humans.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jordan on 2014-08-22.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the caret on the action bar
        getActivity().getActionBar().setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Load the preferences
        addPreferencesFromResource(R.layout.fragment_settings);

        // Set the display name
        updateDisplayName();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences options, String key) {
        if(key.equals("display_name")) {
            sendNameToServer();
        }
    }

    private void sendNameToServer() {
        Preference pref = findPreference("display_name");
        EditTextPreference etp = (EditTextPreference) pref;

        RequestParams params = new RequestParams();
        params.put("name", etp.getText());

        HumansRestClient.instance().put("users/" + HumansRestClient.instance().getUserId(), params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                updateDisplayName();
                Toast.makeText(getActivity(), "Our robots updated your name!",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(getActivity(), "Our robots could not update you name. Try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDisplayName() {
        Preference pref = findPreference("display_name");
        EditTextPreference etp = (EditTextPreference) pref;
        pref.setSummary(etp.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
