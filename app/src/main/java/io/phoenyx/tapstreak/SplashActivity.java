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
    String id, accessToken, username;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("io.phoenyx.tapstreak", MODE_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    id = sharedPreferences.getString("user_id", "");
                    accessToken = sharedPreferences.getString("access_token", "");
                    username = sharedPreferences.getString("username", "");
                    if (id.equals("")) {
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        mainIntent.putExtra("user_id", id);
                        mainIntent.putExtra("access_token", accessToken);
                        mainIntent.putExtra("username", username);
                        startActivity(mainIntent);
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
        }).start();
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
