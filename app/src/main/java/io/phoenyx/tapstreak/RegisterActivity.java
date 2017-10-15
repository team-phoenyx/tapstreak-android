package io.phoenyx.tapstreak;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.phoenyx.tapstreak.jsonmodels.Authentication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    TapstreakService service;
    EditText usernameEditText, passwordEditText, confirmPEditText;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPEditText = (EditText) findViewById(R.id.confirmPWEditText);
        joinButton = (Button) findViewById(R.id.joinButton);

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
                            registerUser(username, password);
                        }
                    }
                }
            }
        });
    }

    private void registerUser(final String username, String password) {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] passHashed = PasswordManager.hash(password.toCharArray(), salt);

        String dbSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
        String dbHashedPass = Base64.encodeToString(passHashed, Base64.NO_WRAP);

        dbSalt = dbSalt.replace('/', '-');
        dbHashedPass.replace('/','-');

        service.registerUser(username, dbHashedPass, dbSalt).enqueue(new Callback<Authentication>() {
            @Override
            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                Authentication authentication = response.body();
                if (authentication.getRespCode().equals("100")) {
                    Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                    friendsIntent.putExtra("user_id", authentication.getUserId());
                    friendsIntent.putExtra("access_token", authentication.getAccessToken());
                    startActivity(friendsIntent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Username taken! Please try again", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}