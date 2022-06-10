package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    private EndlessRecyclerViewScrollLisytener scrollListener;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only ever call `setContentView` once right at the top
        setContentView(R.layout.activity_timeline);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the Action bar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);


        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Recycler view setup: Layout Manager and Adapter
        LinearLayoutManager lls = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(lls);
        rvTweets.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(), lls.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        populateHomeTimeline(null);

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                populateHomeTimeline(null);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scrollListener = new EndlessRecyclerViewScrollLisytener(lls){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Triggered only when new data needs to be appended to the list
                //Add whatever code is needed to append new items to the bottom of the list
                Tweet lastTweetBeingDisplayed = tweets.get(tweets.size()-1);
                String maxId = lastTweetBeingDisplayed.id;

                populateHomeTimeline(maxId);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose) {
            // Compose icon has been selected
            // Navigate to compose activity
            Intent intent = new Intent(this,ComposeActivity.class);
            startActivity(intent);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }else if (item.getItemId() == R.id.miLogout){
            onLogoutButton();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the RV with the tweet
            // Modify data source of tweets
            tweets.add(0, tweet);
            // update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline(String maxId) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"onFailure " + response, throwable);
            }
        });
    }


    public void onLogoutButton() {
        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        finish();

    }


}