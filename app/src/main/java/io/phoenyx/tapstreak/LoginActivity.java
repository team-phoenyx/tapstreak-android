package io.phoenyx.tapstreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import io.phoenyx.tapstreak.jsonmodels.Salt;
import io.phoenyx.tapstreak.jsonmodels.UserID;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    TapstreakService tapstreakService;
    EditText usernameEditText, passwordEditText;
    Button signInButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        tapstreakService = RetrofitClient.getClient("http://tapstreak-backend.azurewebsites.net/").create(TapstreakService.class);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        signInButton = (Button) findViewById(R.id.signInButton);
        registerButton = (Button) findViewById(R.id.registerButton);

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

    private void loginUser(String username, String password) {
        Call<Salt> getSaltCall = tapstreakService.getSalt(username);
        String saltString = "";
        try {
            Salt salt = getSaltCall.execute().body();
            saltString = salt.getSalt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
        byte[] passHashed = PasswordManager.hash(password.toCharArray(), saltBytes);
        String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
        passHashedString = passHashedString.replace("/", "-");

        Call<UserID> getUserID = tapstreakService.login(username, passHashedString);
        try {
            String id = getUserID.execute().body().getUserId();
            if (!id.equals("-1")) {
                Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                friendsIntent.putExtra("user_id", id);
                startActivity(friendsIntent);
                finish();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Login failed", Snackbar.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
