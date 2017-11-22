package io.tapstreak;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import io.tapstreak.json_models.ResponseCode;
import io.tapstreak.json_models.Salt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Terrance on 11/18/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    String userID, accessToken, username;

    ImageButton backButton, logoutButton;
    TextView changeUsernameButton, changePasswordButton;
    EditText usernameEditText, passwordEditText;
    Switch notificationsSwitch, nfcSwitch;
    Button deleteAccountButton;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TapstreakService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        nfcSwitch = findViewById(R.id.nfc_switch);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        changeUsernameButton = findViewById(R.id.username_button);
        changePasswordButton = findViewById(R.id.password_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);

        final Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        userID = extras.getString("user_id");
        accessToken = extras.getString("access_token");

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);
        preferences = getSharedPreferences("io.tapstreak", MODE_PRIVATE);
        editor = preferences.edit();

        usernameEditText.setText(username);

        if (preferences.getBoolean("notifications_enabled", true)) notificationsSwitch.setChecked(true);
        if (preferences.getBoolean("nfc_enabled", true)) nfcSwitch.setChecked(true);
        
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeUsernameButton.getText().toString().equals("change")) {
                    changeUsernameButton.setText("save");
                    usernameEditText.setEnabled(true);
                } else {
                    changeUsernameButton.setText("save");
                    usernameEditText.setEnabled(true);
                    String newUsername = usernameEditText.getText().toString();
                    //TODO update server with username
                }
            }
        });
        
        //TODO usernameedittext addontextchangedlistener, do same thing to check for duplicate
        //circular progressbar, make exception for the users old username
        
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("notifications_enabled", true);
                } else {
                    editor.putBoolean("notifications_enabled", false);
                }
                editor.apply();
            }
        });

        nfcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("nfc_enabled", true);
                } else {
                    editor.putBoolean("nfc_enabled", false);
                }
                editor.apply();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Wipe the stored user from sharedprefs
                editor.remove("user_id");
                editor.remove("access_token");
                editor.remove("notifications_enabled");
                editor.remove("nfc_enabled");
                editor.apply();

                //Start login activity
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //Clears the backstack of activities/fragments
                startActivity(loginIntent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_delete_account, null);
                builder.setView(dialogView);

                final EditText passwordEditText = dialogView.findViewById(R.id.password_edittext);

                builder.setPositiveButton("Delete Account", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button deleteButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String password = passwordEditText.getText().toString();
                                if (password.isEmpty()) return;

                                service.getSalt(username).enqueue(new Callback<Salt>() {
                                    @Override
                                    public void onResponse(Call<Salt> call, Response<Salt> response) {
                                        Salt salt = response.body();

                                        if (salt.getRespCode() == null && !salt.getSalt().isEmpty()) {
                                            String saltString = salt.getSalt();
                                            byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
                                            byte[] passHashed = PasswordManager.hash(password.toCharArray(), saltBytes);
                                            String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
                                            passHashedString = passHashedString.replace("/", "-");
                                            service.deleteUser(userID, accessToken, passHashedString).enqueue(new Callback<ResponseCode>() {
                                                @Override
                                                public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                                                    if (response.body() != null && response.body().getRespCode().equals("100")) {
                                                        //Wipe the stored user from sharedprefs
                                                        editor.remove("user_id");
                                                        editor.remove("access_token");
                                                        editor.remove("notifications_enabled");
                                                        editor.remove("nfc_enabled");
                                                        editor.apply();

                                                        //Start login activity
                                                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //Clears the backstack of activities/fragments
                                                        startActivity(loginIntent);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseCode> call, Throwable t) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Salt> call, Throwable t) {

                                    }
                                });
                            }
                        });
                    }
                });

                dialog.show();
            }
        });
    }
}
