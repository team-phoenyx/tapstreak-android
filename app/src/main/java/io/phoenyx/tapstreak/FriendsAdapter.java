package io.phoenyx.tapstreak;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.CircularPropagation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Calendar;
import java.util.List;

import io.phoenyx.tapstreak.json_models.Friend;

/**
 * Created by terrance on 5/20/17.
 */

public class FriendsAdapter extends ArrayAdapter<Friend> {

    public FriendsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Friend> objects) {
        super(context, resource, objects);
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
            TextView usernameTextView = v.findViewById(R.id.friendNameTextView);
            TextView streakTextView = v.findViewById(R.id.friendStreakTextView);
            CircularProgressBar timeLeftProgressBar = v.findViewById(R.id.timeLeftProgressBar);

            usernameTextView.setText(friend.getUsername());
            streakTextView.setText(String.valueOf(friend.getStreakLength()));

            long timeElapsedMillis = Calendar.getInstance().getTimeInMillis() - friend.getLastStreak();
            double timeElapsedMins = timeElapsedMillis / 60000.0;
            double timeElapsedHours = timeElapsedMins / 60.0;

            if (timeElapsedHours > 8 && timeElapsedHours < 24) timeLeftProgressBar.setColor(Color.rgb(34, 139, 34));
            if (timeElapsedHours >= 24) timeLeftProgressBar.setColor(Color.RED);
            timeLeftProgressBar.setProgressWithAnimation((float) ((32 - timeElapsedHours) / 32.0 * 100));
        }

        return v;
    }
}
