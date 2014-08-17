package com.humansapp.humans;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humansapp.humans.adapters.ConversationsAdapter;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.*;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set the empty list
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        getListView().setEmptyView(emptyText);

        HumansRestClient.instance().get("conversations?user_id=5", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonConversations = response.get("conversations").toString();
                    Gson gson = new Gson();
                    System.out.println(jsonConversations);
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

    public void findHuman(View v) {
        final ProgressDialog progress = ProgressDialog.show(this, "Finding Human", "Please wait while our robots find humans", true);

        HumansRestClient.instance().post("conversations?user_id=5", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
            }
        });
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
