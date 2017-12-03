package io.tapstreak;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import io.tapstreak.json_models.Friend;
import io.tapstreak.json_models.Streak;

/**
 * Created by terrance on 5/20/17.
 */

public class StreaksAdapter extends ArrayAdapter<Streak> {
    Context context;

    public StreaksAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Streak> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.streak_row, null);
        }

        Streak streak = getItem(position);

        if (streak != null) {
            TextView usernameTextView = v.findViewById(R.id.friendNameTextView);
            TextView streakTextView = v.findViewById(R.id.friendStreakTextView);
            ImageView timerImageView = v.findViewById(R.id.timer_imageview);

            usernameTextView.setText(streak.getUsername());
            streakTextView.setText(String.valueOf(streak.getStreakLength()));

            long timeElapsedMillis = System.currentTimeMillis() - streak.getLastStreak();
            double timeElapsedMins = timeElapsedMillis / 60000.0;
            double timeElapsedHours = timeElapsedMins / 60.0;

            if (timeElapsedHours > 12 && timeElapsedHours < 24) {
                streakTextView.setTextColor(getContext().getColor(R.color.colorPrimary));
                timerImageView.setImageResource(R.drawable.timer_ready);
            } else if (timeElapsedHours >= 24) {
                streakTextView.setTextColor(getContext().getColor(R.color.colorError));
                timerImageView.setImageResource(R.drawable.timer_alert);
            } else {
                streakTextView.setTextColor(getContext().getColor(R.color.colorDisabledLight));
                timerImageView.setImageResource(R.drawable.timer_unready);
            }
        }

        return v;
    }
}
