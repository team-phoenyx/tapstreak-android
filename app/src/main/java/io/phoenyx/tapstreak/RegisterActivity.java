package io.phoenyx.tapstreak;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.phoenyx.tapstreak.jsonmodels.UserID;
import io.phoenyx.tapstreak.jsonmodels.UsernameCheck;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    TapstreakService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("").addConverterFactory(GsonConverterFactory.create(gson)).build();
        service = retrofit.create(TapstreakService.class);

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText confirmPEditText = (EditText) findViewById(R.id.confirmPWEditText);
        Button joinButton = (Button) findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                if (!password.equals(confirmPEditText.getText().toString())) {
                    Snackbar.make(findViewById(android.R.id.content), "PasswordManager don't match", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (password.length() < 8 || password.length() > 40) {
                        Snackbar.make(findViewById(android.R.id.content), "Password must be 8-40 long", Snackbar.LENGTH_SHORT).show();
                    } else {
                        String username = usernameEditText.getText().toString();
                        if (username.length() > 40) {
                            Snackbar.make(findViewById(android.R.id.content), "Username too long", Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (isDuplicate(username)) {
                                Snackbar.make(findViewById(android.R.id.content), "Username already taken", Snackbar.LENGTH_SHORT).show();
                            } else {
                                String id = registerUser(username, password);
                                if (!id.equals("-1")) {
                                    Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                                    friendsIntent.putExtra("user_id", id);
                                    startActivity(friendsIntent);
                                    finish();
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private String registerUser(String username, String password) {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] passHashed = PasswordManager.hash(password.toCharArray(), salt);

        String dbSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
        String dbHashedPass = Base64.encodeToString(passHashed, Base64.NO_WRAP);

        dbSalt = dbSalt.replace('/', '-');
        dbHashedPass.replace('/','-');

        Call<UserID> registerUserCall = service.makeUser(username, dbHashedPass, dbSalt);
        try {
            UserID user = registerUserCall.execute().body();
            return user.getUserId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    private boolean isDuplicate(String username) {
        Call<UsernameCheck> duplicate = service.checkDuplicate(username);
        //final boolean[] dupe = new boolean[1];
        try {
            UsernameCheck usernameCheck = duplicate.execute().body();
            return usernameCheck.alreadyExists().equals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

        /*
        duplicate.enqueue(new Callback<UsernameCheck>() {
            @Override
            public void onFailure(Call<UsernameCheck> call, Throwable t) {

            }

            @Override
            public void onResponse(Call<UsernameCheck> call, Response<UsernameCheck> response) {
                dupe[0] = response.body().equals("true");
            }
        });
        return dupe[0];
        */

    }
}