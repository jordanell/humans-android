package com.humansapp.humans;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humansapp.humans.adapters.ConversationsAdapter;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.models.User;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.*;

public class MainActivity extends ListActivity {

    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set the empty list
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        getListView().setEmptyView(emptyText);

        loadConversations();
    }

    private void loadConversations() {
        // Ensure we have a valid user
        if(userId == null) {
            SharedPreferences userPrefs = this.getPreferences(Context.MODE_PRIVATE);
            String token = userPrefs.getString("id", null);

            System.out.println("Token: " + token);

            if(token == null) {
                System.out.println("Getting user");
                createUser();
                return;
            } else {
                this.userId = token;
            }
        }

        RequestParams params = new RequestParams();
        params.put("user_id", userId);

        HumansRestClient.instance().get("conversations", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonConversations = response.get("conversations").toString();
                    Gson gson = new Gson();
                    Conversation[] conversations = gson.fromJson(jsonConversations, Conversation[].class);

                    ConversationsAdapter adapter =
                            new ConversationsAdapter(getBaseContext(), conversations);

                    ProgressBar progress = (ProgressBar) findViewById(R.id.list_loading);
                    progress.setVisibility(View.GONE);

                    setListAdapter(adapter);
                    getListView().setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    // Something went wrong
                }
            }
        });
    }

    private void createUser() {
        final Activity activity = this;

        HumansRestClient.instance().post("users", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonUser = response.get("user").toString();
                    Gson gson = new Gson();
                    User user = gson.fromJson(jsonUser, User.class);

                    // Store the id in preferences
                    SharedPreferences userPrefs = activity.getPreferences(Context.MODE_PRIVATE);
                    userPrefs.edit().putString("id", user.getId()).commit();

                    // Store the id in memory
                    userId = user.getId();

                    loadConversations();
                } catch (JSONException e) {
                    // Something went wrong
                }
            }
        });
    }

    public void findHuman(View v) {
        final ProgressDialog progress = ProgressDialog.show(this, "Finding Human", "Please wait while our robots find humans", true);

        String url = "conversations/?user_id=" + userId;

        HumansRestClient.instance().post(url, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
