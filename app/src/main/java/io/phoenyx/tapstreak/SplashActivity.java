package io.phoenyx.tapstreak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {
    String id;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("io.phoenyx.tapstreak", MODE_PRIVATE);

        if (isConnected()) {
            int userID = sharedPreferences.getInt("user_id", -1);
            if (userID < 0) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
                Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                friendsIntent.putExtra("user_id", userID);
                startActivity(friendsIntent);
                finish();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "No Connection", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }).show();
        }


    }
  
    private boolean isConnected() {
        try{
            URL myUrl = new URL("http://tapstreak-backend.azurewebsites.net/");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(2000);
            connection.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
