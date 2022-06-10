package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // Pass in context and list of tweets

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // Clean all the elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at the position
        Tweet tweet = tweets.get(position);

        // Bind the tweet
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // Define the viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView imageTweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            imageTweet = itemView.findViewById(R.id.imageTweet);

        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            String format = tweet.user.screenName + " " + tweet.date;
            tvScreenName.setText(format);
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(90)).into(ivProfileImage);

            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(90)).into(ivProfileImage);

            if (tweet.imageURL != null) {
                Glide.with(context).load(tweet.imageURL).transform(new RoundedCorners(90)).into(imageTweet);
                imageTweet.setVisibility(View.VISIBLE);
            } else {
                imageTweet.setVisibility(View.GONE);
            }
        }


    }
}




