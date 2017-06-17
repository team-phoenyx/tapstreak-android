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

import io.phoenyx.tapstreak.jsonmodels.GetSalt;
import io.phoenyx.tapstreak.jsonmodels.LoginUser;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    TapstreakService tapstreakService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tapstreakService = RetrofitClient.getClient("http://tapstreak-backend.azurewebsites.net/").create(TapstreakService.class);

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        Button signInButton = (Button) findViewById(R.id.signInButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

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
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(username, password);
            }
        });

    }

    private void loginUser(String username, String password) {
        Call<GetSalt> getSaltCall = tapstreakService.getSalt(username);
        String saltString = "";
        try {
            GetSalt salt = getSaltCall.execute().body();
            saltString = salt.getSalt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
        byte[] passHashed = Passwords.hash(password.toCharArray(), saltBytes);
        String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
        passHashedString = passHashedString.replace("/", "-");

        Call<LoginUser> getUserID = tapstreakService.login(username, passHashedString);
        try {
            LoginUser userID = getUserID.execute().body();
            String id = userID.getUserId();
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
