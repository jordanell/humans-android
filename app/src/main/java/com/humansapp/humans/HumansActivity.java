package com.humansapp.humans;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.humansapp.humans.fragments.ConversationsListFragment;
import com.humansapp.humans.fragments.UserSetupFragment;
import com.humansapp.humans.rest.HumansRestClient;
import com.humansapp.humans.stores.DataStore;
import com.humansapp.humans.websocket.HumansWebSocketClient;

/**
 * Created by jordan on 2014-08-18.
 */
public class HumansActivity extends ActionBarActivity {

    DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create data store
        dataStore = new DataStore();

        // Set content view
        setContentView(R.layout.activity_humans);

        // Setup user and go to correct fragment
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if(userId == null) {
            // Go to user setup
            UserSetupFragment setupFragment = new UserSetupFragment();
            changeFragment(setupFragment, false);
        } else {
            // Setup the user id
            HumansRestClient.instance().setUserId(userId);

            // Connect the socket
            HumansWebSocketClient.instance(this).connectSocket();

            // Go directly to conversations fragment
            ConversationsListFragment conversations = new ConversationsListFragment();
            changeFragment(conversations, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.humans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if(fm.getBackStackEntryCount() == 0) {
            new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit Humans?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
        } else {
            fm.popBackStack();
        }
    }

    /**
     * Swap the current fragment to be displayed at the root of this activity.
     * @param fragment The new fragment to display.
     * @param saveState Should the state be saved.
     */
    public void changeFragment(Fragment fragment, boolean saveState) {
        FragmentManager fragManager = getFragmentManager();
        if (saveState) {
            // Switch fragments.
            fragManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            fragManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commitAllowingStateLoss();
        }
    }

    /**
     * Return the DataStore being used in this activity.
     * @return The DataStore.
     */
    public DataStore getDataStore() {
        return dataStore;
    }
}
