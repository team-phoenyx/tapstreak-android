package io.phoenyx.tapstreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.phoenyx.tapstreak.json_models.Salt;
import io.phoenyx.tapstreak.json_models.Authentication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TapstreakService tapstreakService;
    EditText usernameEditText, passwordEditText;
    Button signInButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tapstreakService = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        registerButton = (Button) findViewById(R.id.join_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

    }

    private void loginUser(final String username, final String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Fields empty", Snackbar.LENGTH_SHORT).show();
            return;
        }

        tapstreakService.getSalt(username).enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                Salt salt = response.body();

                if (salt.getRespCode().equals("100")) {
                    String saltString = salt.getSalt();
                    byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
                    byte[] passHashed = PasswordManager.hash(password.toCharArray(), saltBytes);
                    String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
                    passHashedString = passHashedString.replace("/", "-");
                    authenticateUser(username, passHashedString);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Username/password is incorrect", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateUser(String username, String passHashedString) {
        tapstreakService.loginUser(username, passHashedString).enqueue(new Callback<Authentication>() {
            @Override
            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                Authentication authentication = response.body();

                if (authentication.getRespCode().equals("100")) {
                    String id = authentication.getUserId();
                    String accessToken = authentication.getAccessToken();
                    Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                    friendsIntent.putExtra("user_id", id);
                    friendsIntent.putExtra("access_token", accessToken);
                    startActivity(friendsIntent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Username/password is incorrect", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
