package io.tapstreak;

import android.content.Context;
import android.location.Location;
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

public class FriendsAdapter extends ArrayAdapter<Friend> {
    Context context;
    Location location;

    public FriendsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Friend> objects, Location location) {
        super(context, resource, objects);
        this.context = context;
        this.location = location;
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
            TextView usernameTextView = v.findViewById(R.id.friend_textview);
            TextView distanceTextView = v.findViewById(R.id.distance_textview);
            TextView timeTextView = v.findViewById(R.id.time_textview);

            usernameTextView.setText(friend.getUsername());

            if (friend.getLastSeenTime() != null && friend.getLat() != null && friend.getLon() != null && location != null) {
                double lastSeenDiffMinutes = (System.currentTimeMillis() - Double.parseDouble(friend.getLastSeenTime())) / 1000 / 60.0;
                double distanceDiffMiles = distanceDiffMiles(Double.parseDouble(friend.getLat()), Double.parseDouble(friend.getLon()), location.getLatitude(), location.getLongitude());
                if (lastSeenDiffMinutes > 10.0 && distanceDiffMiles > 10.0) {
                    distanceTextView.setVisibility(View.GONE);
                    timeTextView.setVisibility(View.GONE);
                } else {
                    timeTextView.setVisibility(View.VISIBLE);
                    distanceTextView.setVisibility(View.VISIBLE);
                    if (distanceDiffMiles < 0.1) distanceTextView.setText("at your location");
                    else distanceTextView.setText(Math.round(distanceDiffMiles * 10.0) / 10.0 + " mile(s) from you");

                    if (lastSeenDiffMinutes < 1.0) timeTextView.setText("as of now");
                    else if (lastSeenDiffMinutes < 1.5) timeTextView.setText("as of 1 minute ago");
                    else timeTextView.setText("as of " + Math.round(lastSeenDiffMinutes) + " minutes ago");
                }
            } else {
                distanceTextView.setVisibility(View.GONE);
                timeTextView.setVisibility(View.GONE);
            }
        }

        return v;
    }

    private double distanceDiffMiles(double lat1, double lon1, double lat2, double lon2) {
        int r = 6371; //radius of earth, in km
        double dLat = toRad(lat2 - lat1);
        double dLon = toRad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return r * c / 1.609344; //returns miles

    }

    private double toRad(double value) {
        return value * Math.PI / 180;
    }
}
