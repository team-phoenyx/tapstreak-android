package io.tapstreak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.net.URL;
import java.net.URLConnection;

import io.tapstreak.json_models.Authentication;
import io.tapstreak.json_models.ResponseCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    String id, accessToken, username;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("io.tapstreak", MODE_PRIVATE);

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

                        TapstreakService service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

                        service.reauthenticate(id, accessToken).enqueue(new Callback<Authentication>() {
                            @Override
                            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                                if (response != null && response.body() != null && "100".equals(response.body().getRespCode())) {
                                    Authentication authentication = response.body();
                                    id = authentication.getUserId();
                                    accessToken = authentication.getAccessToken();
                                    username = authentication.getUsername();

                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    mainIntent.putExtra("user_id", id);
                                    mainIntent.putExtra("access_token", accessToken);
                                    mainIntent.putExtra("username", username);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                    sharedPreferences.edit().remove("user_id").remove("access_token").remove("username").apply();

                                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Authentication> call, Throwable t) {
                                sharedPreferences.edit().remove("user_id").remove("access_token").remove("username").apply();

                                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        });
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
