package io.phoenyx.tapstreak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.phoenyx.tapstreak.jsonmodels.GetFriends;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {

    TapstreakService service;
    String userID;
    ArrayList<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("user_id");

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("").addConverterFactory(GsonConverterFactory.create(gson)).build();
        service = retrofit.create(TapstreakService.class);

        Call<List<GetFriends>> getFriendsCall = service.getFriends(userID);
        try {
            List<GetFriends> friendsCallback = getFriendsCall.execute().body();

            for (GetFriends friendCallback : friendsCallback) {
                Friend friend = new Friend(friendCallback.getUserId(), friendCallback.getUsername(), Integer.parseInt(friendCallback.getStreakLength()), Integer.parseInt(friendCallback.getLastStreak()), Integer.parseInt(friendCallback.getFirstStreak()));
                friends.add(friend);
            }

            FriendsAdapter friendsAdapter = new FriendsAdapter(this, R.layout.friend_row, friends);

            ListView friendsListView = (ListView) findViewById(R.id.friendsListView);

            friendsListView.setAdapter(friendsAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
