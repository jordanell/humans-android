package com.humansapp.humans.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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

    // Timer
    private static int FIND_TIME_OUT = 2500;
    long startTime;

    // Adapter
    private ConversationsAdapter adapter;

    // Layout
    private ListView list;
    private LinearLayout progress;
    private RelativeLayout loading;
    private TextView empty;
    private RelativeLayout error;
    private ProgressDialog findProgress;

    private View view;

    // Options menu
    private final int CM_VIEW = 1;
    private final int CM_LEAVE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set the options menu
        setHasOptionsMenu(true);

        // Get the view
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        this.view = view;

        // Get all the layout variables
        loading = (RelativeLayout) view.findViewById(R.id.loading);
        error = (RelativeLayout) view.findViewById(R.id.error);
        empty = (TextView) view.findViewById(R.id.empty);
        list = (ListView) view.findViewById(R.id.conversations_list);

        // Set up the conversation list adapter
        adapter = ((HumansActivity)getActivity()).getDataStore().getConversationsAdapter();
        list.setAdapter(adapter);

        //Load the conversations
        loadConversations(true);

        // Set button listener
        view.findViewById(R.id.btn_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findHuman(view);
            }
        });

        // Set list click listener
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
                    loadConversations(false);
                }
            }
        });

        // Set up the conversations context menu
        registerForContextMenu(list);

        // Set up the observer for conversations
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (adapter.getCount() > 0) {
                    empty.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
                super.onChanged();
            }
        });

        return view;
    }

    /**
     * Load the conversations for the current page.
     */
    private void loadConversations(boolean reset) {
        if (reset) {
            // Reset the data store
            ((HumansActivity)getActivity()).getDataStore().getConversationsAdapter().clear();

            // Show we are loading something for the first time
            loading.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        }

        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());
        params.put("page", this.page);

        HumansRestClient.instance().get("conversations", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ConversationsListFragment.this.fetching = false;

                    String jsonConversations = response.get("conversations").toString();
                    Gson gson = new Gson();
                    Conversation[] conversations = gson.fromJson(jsonConversations, Conversation[].class);
                    ArrayList<Conversation> cList = new ArrayList<Conversation>(Arrays.asList(conversations));

                    if  (cList.size() == 0) {
                        ConversationsListFragment.this.complete = true;
                    }

                    ((HumansActivity)getActivity()).getDataStore().addConversations(cList);

                    loading.setVisibility(View.GONE);
                    error.setVisibility(View.GONE);

                    if(list.getAdapter().getCount() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        list.setVisibility(View.VISIBLE);
                    }

                    if (ConversationsListFragment.this.page == 1) {
                        list.setSelection(0);
                    }
                } catch (JSONException e) {
                    ConversationsListFragment.this.fetching = false;
                    showError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                ConversationsListFragment.this.fetching = false;
                showError();
            }
        });
    }

    /**
     * Initiate the finding of a human.
     * @param v The view in which this was initiated.
     */
    private void findHuman(View v) {
        // Show loading and start timer
        findProgress = ProgressDialog.show(getActivity(), "Finding Human", "Please wait while our robots find humans", true);
        this.startTime = System.currentTimeMillis();

        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());

        HumansRestClient.instance().post("conversations", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String jsonConversations = response.get("conversation").toString();
                    Gson gson = new Gson();
                    final Conversation conversation = gson.fromJson(jsonConversations, Conversation.class);

                    ((HumansActivity)getActivity()).getDataStore().addConversation(conversation);

                    if (System.currentTimeMillis()-startTime > FIND_TIME_OUT) {
                        openConversation(conversation);
                    } else {
                        Handler handler = new Handler();
                        Runnable delayedOpen = new Runnable() {
                            @Override
                            public void run() {
                                openConversation(conversation);
                            }
                        };

                        handler.postDelayed(delayedOpen, FIND_TIME_OUT - (System.currentTimeMillis()-startTime));
                    }
                } catch (JSONException e) {
                    delayedFindError("The human found was no good, try again");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                delayedFindError("Robots failed finding a human, try again.");
            }
        });
    }

    /**
     * Displays an error after a amount of time has passed.
     * @param toastMessage The message to be displayed in the error toast.
     */
    private void delayedFindError(String toastMessage) {
        if (System.currentTimeMillis()-startTime > FIND_TIME_OUT) {
            findError(toastMessage);
        } else {
            final String message = toastMessage;

            Handler handler = new Handler();
            Runnable delayedOpen = new Runnable() {
                @Override
                public void run() {
                    findError(message);
                }
            };

            handler.postDelayed(delayedOpen, FIND_TIME_OUT - (System.currentTimeMillis()-startTime));
        }
    }

    /**
     * Displays a toast with an error.
     * @param toastMessage The message to be displayed in the error toast.
     */
    private void findError(String toastMessage) {
        findProgress.dismiss();
        Toast.makeText(getActivity(), toastMessage,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Displays a full screen error.
     */
    private void showError() {
        if (findProgress != null) {
            findProgress.dismiss();
        }

        loading.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);

        error.setVisibility(View.VISIBLE);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry();
            }
        });
    }

    /**
     * Initiates a new attempt of loading conversations.
     */
    private void retry() {
        error.setVisibility(View.GONE);
        error.setOnClickListener(null);

        loadConversations(true);
    }

    /**
     * Opens a conversation in the conversation fragment.
     * @param conversation The conversation to open.
     */
    private void openConversation(Conversation conversation) {
        if (findProgress != null) {
            findProgress.dismiss();
        }

        Bundle b = new Bundle();
        b.putString("id", conversation.getId());
        b.putString("name", conversation.getName(HumansRestClient.instance().getUserId()));

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
                adapter.clear();
                this.page = 1;
                this.complete = false;
                loadConversations(true);
                break;
            case R.id.action_settings:
                SettingsFragment fragment = new SettingsFragment();
                ((HumansActivity)getActivity()).changeFragment(fragment, true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Given a conversation, leaves that conversation.
     * @param conversation The conversation to leave.
     */
    private void leaveConversation(Conversation conversation) {
        RequestParams params = new RequestParams();
        params.put("user_id", HumansRestClient.instance().getUserId());
        params.put("conversation_id", conversation.getId());

        final Conversation c = conversation;
        HumansRestClient.instance().put("conversations/leave", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ((HumansActivity)getActivity()).getDataStore().removeConversation(c.getId());

                if(adapter.getCount() == 0) {
                    list.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(getActivity(), "Could not leave this human. Try Again",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
