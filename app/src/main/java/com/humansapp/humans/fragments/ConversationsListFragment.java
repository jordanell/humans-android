package com.humansapp.humans.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humansapp.humans.HumansActivity;
import com.humansapp.humans.R;
import com.humansapp.humans.adapters.ConversationsAdapter;
import com.humansapp.humans.models.Conversation;
import com.humansapp.humans.rest.HumansRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationsListFragment extends Fragment {

    private LinearLayout progress;
    private ConversationsAdapter adapter;
    private ListView list;
    private RelativeLayout loading;
    private RelativeLayout content;
    private TextView empty;
    private RelativeLayout error;

    private View view;

    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        this.view = view;

        // Set up the conversation list adapter
        if(adapter == null) {
            adapter = new ConversationsAdapter(getActivity(), new Conversation[0]);
        }
        final ListView list = (ListView) view.findViewById(R.id.conversations_list);
        this.list = list;
        list.setAdapter(adapter);

        loading = (RelativeLayout) view.findViewById(R.id.loading);
        content = (RelativeLayout) view.findViewById(R.id.content);
        empty = (TextView) view.findViewById(R.id.empty);
        error = (RelativeLayout) view.findViewById(R.id.error);

        //Load the conversations if needed
        if(getArguments() != null && getArguments().getBoolean("new")) {
            loading.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            empty.setVisibility(View.VISIBLE);
        } else {
            loadConversations();
        }

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        list.setAdapter(null);
                        loadConversations();
                    }
                })
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);


        view.findViewById(R.id.btn_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findHuman(view);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Conversation conversation = (Conversation) list.getItemAtPosition(i);

                Bundle b = new Bundle();
                b.putString("id", conversation.getId());
                b.putString("name", conversation.getName());

                ConversationFragment fragment = new ConversationFragment();
                fragment.setArguments(b);

                ((HumansActivity)getActivity()).changeFragment(fragment, true);
            }
        });

        return view;
    }

    private void loadConversations() {
        // Show we are loading something
        loading.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        error.setVisibility(View.GONE);

        // Clear old list
        adapter = new ConversationsAdapter(getActivity(), new Conversation[0]);

        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());

        HumansRestClient.instance().get("conversations", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonConversations = response.get("conversations").toString();
                    Gson gson = new Gson();
                    Conversation[] conversations = gson.fromJson(jsonConversations, Conversation[].class);

                    ConversationsAdapter ad =
                            new ConversationsAdapter(getActivity(), conversations);

                    adapter = ad;
                    list.setAdapter(adapter);

                    if(ad.getCount() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        list.setVisibility(View.VISIBLE);
                    }

                    loading.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);

                    mPullToRefreshLayout.setRefreshComplete();
                } catch (JSONException e) {
                    // Something went wrong
                    progress.setVisibility(View.GONE);
                    showError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                progress = (LinearLayout) view.findViewById(R.id.header_progress);
                progress.setVisibility(View.GONE);
                showError();
            }
        });
    }

    private void findHuman(View v) {
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Finding Human", "Please wait while our robots find humans", true);

        String url = "conversations/?user_id=";
        url += HumansRestClient.instance().getUserId();

        HumansRestClient.instance().post(url, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
            }
        });
    }

    private void showError() {
        error.setVisibility(View.VISIBLE);

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry();
            }
        });
    }

    private void retry() {
        error.setVisibility(View.GONE);

        error.setOnClickListener(null);

        loadConversations();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_conversations_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                list.setAdapter(null);
                loadConversations();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
