package com.humansapp.humans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.humansapp.humans.fragments.ConversationsListFragment;

/**
 * Created by jordan on 2014-08-18.
 */
public class HumansActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_humans);

        // Go directly to conversations fragment
        ConversationsListFragment conversations = new ConversationsListFragment();
        changeFragment(conversations, false);
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
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

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
            super.onBackPressed();
        }
    }

    public void changeFragment(Fragment fragment, boolean saveState) {
        FragmentManager fragManager = getSupportFragmentManager();
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
}
