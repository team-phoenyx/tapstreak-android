package io.tapstreak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.tapstreak.json_models.Salt;
import io.tapstreak.json_models.Authentication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TapstreakService tapstreakService;
    EditText usernameEditText, passwordEditText;
    Button signInButton, registerButton;
    ProgressBar loadingProgressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tapstreakService = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        signInButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.join_button);
        loadingProgressCircle = findViewById(R.id.loading_progresscircle);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !passwordEditText.getText().toString().isEmpty()) {
                    signInButton.setVisibility(View.VISIBLE);
                    signInButton.setEnabled(true);
                }
                else {
                    signInButton.setVisibility(View.INVISIBLE);
                    signInButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !usernameEditText.getText().toString().isEmpty()) {
                    signInButton.setVisibility(View.VISIBLE);
                    signInButton.setEnabled(true);
                }
                else {
                    signInButton.setVisibility(View.INVISIBLE);
                    signInButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);
        loadingProgressCircle.setVisibility(View.VISIBLE);

        tapstreakService.getSalt(username).enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                Salt salt = response.body();

                if (salt.getRespCode() == null && !salt.getSalt().isEmpty()) {
                    String saltString = salt.getSalt();
                    byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
                    byte[] passHashed = PasswordManager.hash(password.toCharArray(), saltBytes);
                    String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
                    passHashedString = passHashedString.replace("/", "-");
                    authenticateUser(username, passHashedString);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Username/password is incorrect", Snackbar.LENGTH_SHORT).show();
                    signInButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    loadingProgressCircle.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
                registerButton.setEnabled(true);
                loadingProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void authenticateUser(final String username, String passHashedString) {
        tapstreakService.loginUser(username, passHashedString).enqueue(new Callback<Authentication>() {
            @Override
            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                Authentication authentication = response.body();

                if (authentication.getRespCode().equals("100")) {
                    String id = authentication.getUserId();
                    String accessToken = authentication.getAccessToken();

                    SharedPreferences.Editor editor = getSharedPreferences("io.tapstreak", Context.MODE_PRIVATE).edit();
                    editor.putString("user_id", id);
                    editor.putString("access_token", accessToken);
                    editor.putString("username", username);
                    editor.apply();

                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.putExtra("user_id", id);
                    mainIntent.putExtra("access_token", accessToken);
                    mainIntent.putExtra("username", username);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Username/password is incorrect", Snackbar.LENGTH_SHORT).show();
                }
                signInButton.setEnabled(true);
                registerButton.setEnabled(true);
                loadingProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
                registerButton.setEnabled(true);
                loadingProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
}
