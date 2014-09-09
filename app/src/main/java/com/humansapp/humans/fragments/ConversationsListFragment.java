package com.humansapp.humans.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jordan on 2014-08-18.
 */
public class ConversationsListFragment extends InifiniteScrollFragment {

    private LinearLayout progress;
    private ConversationsAdapter adapter;
    private ListView list;
    private RelativeLayout loading;
    private RelativeLayout content;
    private TextView empty;
    private RelativeLayout error;

    private View view;

    private final int CM_VIEW = 1;
    private final int CM_LEAVE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        this.view = view;

        // Set up the conversation list adapter
        if(adapter == null) {
            adapter = new ConversationsAdapter(getActivity(), new ArrayList<Conversation>());
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
                openConversation(conversation);
            }
        });

        // Set infinite scroll listener
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if ( i3 != 0 && (float)i / (float)i3 > 0.5f && ConversationsListFragment.this.fetching == false &&
                        ConversationsListFragment.this.complete == false) {
                    System.out.println("Loading more");
                    ConversationsListFragment.this.page++;
                    loadConversations();
                }
            }
        });

        // Set up the conversations context menu
        registerForContextMenu(list);

        return view;
    }

    private void loadConversations() {
        if (list.getAdapter().getCount() == 0) {
            // Show we are loading something for the first time
            loading.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            error.setVisibility(View.GONE);
        }

        // Set the infinite scroll to loading
        this.fetching = true;
        final ViewGroup footerView = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.list_footer, list, false);
        list.addFooterView(footerView);

        // Clear old list
        adapter = new ConversationsAdapter(getActivity(), new ArrayList<Conversation>());

        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());
        params.put("page", this.page);

        HumansRestClient.instance().get("conversations", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    list.removeFooterView(footerView);

                    String jsonConversations = response.get("conversations").toString();
                    Gson gson = new Gson();
                    Conversation[] conversations = gson.fromJson(jsonConversations, Conversation[].class);

                    ArrayList<Conversation> cList = new ArrayList<Conversation>(Arrays.asList(conversations));

                    if  (cList.size() == 0) {
                        ConversationsListFragment.this.complete = true;
                    }

                    ((ConversationsAdapter)((HeaderViewListAdapter)list.getAdapter()).getWrappedAdapter()).addAll(cList);

                    if(list.getAdapter().getCount() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        list.setVisibility(View.VISIBLE);
                    }

                    if (ConversationsListFragment.this.page == 1) {
                        list.setSelection(0);
                    }

                    loading.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);

                    ConversationsListFragment.this.fetching = false;
                } catch (JSONException e) {
                    // Something went wrong
                    ConversationsListFragment.this.fetching = false;
                    list.removeFooterView(footerView);
                    showError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                progress = (LinearLayout) view.findViewById(R.id.header_progress);
                progress.setVisibility(View.GONE);

                ConversationsListFragment.this.fetching = false;
                list.removeFooterView(footerView);

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
                try {
                    String jsonConversations = response.get("conversation").toString();
                    Gson gson = new Gson();
                    Conversation conversation = gson.fromJson(jsonConversations, Conversation.class);

                    openConversation(conversation);
                } catch (JSONException e) {
                    progress.dismiss();
                    Toast.makeText(getActivity(), "The human found was no good. Try Again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Failed to find human. Try Again",
                        Toast.LENGTH_LONG).show();
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

    private void openConversation(Conversation conversation) {
        Bundle b = new Bundle();
        b.putString("id", conversation.getId());
        b.putString("name", conversation.getName());

        ConversationFragment fragment = new ConversationFragment();
        fragment.setArguments(b);

        ((HumansActivity)getActivity()).changeFragment(fragment, true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, CM_VIEW, Menu.NONE, "View");
        menu.add(Menu.NONE, CM_LEAVE, Menu.NONE, "Leave");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_conversations_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Conversation conversation = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case CM_VIEW:
                openConversation(conversation);
                return true;
            case CM_LEAVE:
                new AlertDialog.Builder(getActivity())
                    .setTitle("Leave")
                    .setMessage("Are you sure you want to leave this human?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveConversation(conversation);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                ((ConversationsAdapter)((HeaderViewListAdapter)list.getAdapter()).getWrappedAdapter()).clear();
                this.page = 1;
                this.complete = false;
                loadConversations();
                break;
            case R.id.action_settings:
                SettingsFragment fragment = new SettingsFragment();
                ((HumansActivity)getActivity()).changeFragment(fragment, true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void leaveConversation(Conversation conversation) {
        StringBuilder url = new StringBuilder();
        url.append("conversations/leave?user_id=");
        url.append(HumansRestClient.instance().getUserId());
        url.append("&conversation_id=");
        url.append(conversation.getId());

        HumansRestClient.instance().put(url.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Flash something here
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // Flash something here
            }
        });

        adapter.remove(conversation);

        if(adapter.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }
}
