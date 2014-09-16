package com.humansapp.humans.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.models.User;
import com.humansapp.humans.rest.HumansRestClient;
import com.humansapp.humans.websocket.HumansWebSocketClient;
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

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_user_setup, container, false);
        this.view = view;

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

                    // Place user id in rest client
                    HumansRestClient.instance().setUserId(user.getId());

                    // Connect the socket
                    HumansWebSocketClient.instance((HumansActivity)getActivity()).connectSocket();

                    setupComplete = true;

                    if(System.currentTimeMillis()-startTime > SETUP_TIME_OUT) {
                        goToConversations();
                    }
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

    private void showError() {
        RelativeLayout error = (RelativeLayout) view.findViewById(R.id.error_layout);
        error.setVisibility(View.VISIBLE);

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry();
            }
        });
    }

    private void retry() {
        RelativeLayout error = (RelativeLayout) view.findViewById(R.id.error_layout);
        error.setVisibility(View.GONE);

        error.setOnClickListener(null);

        createUser();
    }

    private void goToConversations() {
        // Remove the timer function
        this.handler.removeCallbacks(this.runnable);

        ConversationsListFragment fragment = new ConversationsListFragment();

        Bundle b = new Bundle();
        b.putBoolean("new", true);
        fragment.setArguments(b);

        ((HumansActivity)getActivity()).changeFragment(fragment, false);
    }
}
