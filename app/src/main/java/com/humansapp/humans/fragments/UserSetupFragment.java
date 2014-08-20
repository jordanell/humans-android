package com.humansapp.humans.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.models.User;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jordan on 2014-08-19.
 */
public class UserSetupFragment extends Fragment {

    // Timer
    private static int SETUP_TIME_OUT = 3000;
    long startTime;

    // Setup
    private boolean setupComplete = false;

    // Handler stuff
    private Handler handler;
    private Runnable runnable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_user_setup, container, false);

        this.startTime = System.currentTimeMillis();

        Handler handler = new Handler();

        Runnable delayedTransition = new Runnable() {
            @Override
            public void run() {
                if(setupComplete) {
                    goToConversations();
                }

            }
        };

        this.handler = handler;
        this.runnable = delayedTransition;
        handler.postDelayed(delayedTransition, SETUP_TIME_OUT);

        createUser();

        return view;
    }

    private void createUser() {
        HumansRestClient.instance().post("users", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonUser = response.get("user").toString();
                    Gson gson = new Gson();

                    User user = gson.fromJson(jsonUser, User.class);

                    // Commit the user id to preferences
                    SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                    prefs.edit().putString("userId", user.getId()).commit();

                    setupComplete = true;

                    if(System.currentTimeMillis()-startTime > SETUP_TIME_OUT) {
                        goToConversations();
                    }
                } catch (JSONException e) {
                    // Something went wrong
                }
            }
        });
    }

    private void goToConversations() {
        // Remove the timer function
        this.handler.removeCallbacks(this.runnable);

        ConversationsListFragment fragment = new ConversationsListFragment();
        ((HumansActivity)getActivity()).changeFragment(fragment, false);
    }
}
