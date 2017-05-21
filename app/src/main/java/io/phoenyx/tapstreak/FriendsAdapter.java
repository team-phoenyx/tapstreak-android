package io.phoenyx.tapstreak;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by terrance on 5/20/17.
 */

public class FriendsAdapter extends ArrayAdapter<Friend> {
    TapstreakService service;
    String id;

    public FriendsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Friend> objects, String id) {
        super(context, resource, objects);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("").addConverterFactory(GsonConverterFactory.create(gson)).build();
        service = retrofit.create(TapstreakService.class);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.friend_row, null);
        }

        Friend friend = getItem(position);

        if (friend != null) {
            TextView usernameTextView = (TextView) v.findViewById(R.id.friendNameTextView);
            TextView streakTextView = (TextView) v.findViewById(R.id.friendStreakTextView);
            ProgressBar timeLeftProgressBar = (ProgressBar) v.findViewById(R.id.timeLeftProgressBar);

            usernameTextView.setText(friend.getUsername());
            streakTextView.setText(friend.getStreak());

            long timeElapsedMillis = Calendar.getInstance().getTimeInMillis() - friend.getLast_streak();
            int timeElapsedMins = (int) (timeElapsedMillis / 60000);

            if (timeElapsedMins > 1440) {
                service.removeStreak(id, friend.getId());
                streakTextView.setText("");
                timeLeftProgressBar.setProgress(0);
            } else {
                timeLeftProgressBar.setProgress(1440 - timeElapsedMins);
            }
        }

        return v;
    }
}
