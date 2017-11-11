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
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
        /*
        if (isConnected()) {
            id = sharedPreferences.getString("user_id", "");
            if (id.equals("")) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
                Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                friendsIntent.putExtra("user_id", id);
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
        */

    }
  
    private boolean isConnected() {
        try{
            URL myUrl = new URL(getResources().getString(R.string.api_base_url));
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
