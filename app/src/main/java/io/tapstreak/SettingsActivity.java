package io.tapstreak;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

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
    Switch notificationsSwitch, nfcSwitch, locationSwitch;
    Button deleteAccountButton;
    ProgressBar checkUNProgressCircle;
    ImageView uniqueUNImageView, duplicateUNImageView;
    Timer timer;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TapstreakService service;

    private final long DELAY = 500;
    private static final int REQUEST_FINE_LOCATION_REQUEST = 39173;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        nfcSwitch = findViewById(R.id.nfc_switch);
        locationSwitch = findViewById(R.id.location_switch);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        changeUsernameButton = findViewById(R.id.username_button);
        changePasswordButton = findViewById(R.id.password_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        checkUNProgressCircle = findViewById(R.id.check_username_progresscircle);
        uniqueUNImageView = findViewById(R.id.username_unique_imageview);
        duplicateUNImageView = findViewById(R.id.username_duplicate_imageview);

        final Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        userID = extras.getString("user_id");
        accessToken = extras.getString("access_token");

        setResult(RESULT_OK, new Intent().putExtra("access_token", accessToken).putExtra("username", username));

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);
        preferences = getSharedPreferences("io.tapstreak", MODE_PRIVATE);
        editor = preferences.edit();

        usernameEditText.setText(username);

        if (preferences.getBoolean("notifications_enabled", true)) notificationsSwitch.setChecked(true);
        if (preferences.getBoolean("nfc_enabled", true)) nfcSwitch.setChecked(true);
        if (preferences.getBoolean("location_enabled", false)) locationSwitch.setChecked(true);
        
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!changePasswordButton.isEnabled()) return;
                if (changeUsernameButton.getText().toString().equals("change")) {
                    changeUsernameButton.setText("save");
                    usernameEditText.setEnabled(true);
                } else {
                    if (usernameEditText.getText().toString().equals(username)) {
                        usernameEditText.setText(username);
                        uniqueUNImageView.setVisibility(View.INVISIBLE);
                        duplicateUNImageView.setVisibility(View.INVISIBLE);
                        checkUNProgressCircle.setVisibility(View.INVISIBLE);
                        changeUsernameButton.setEnabled(true);
                        changeUsernameButton.setText("change");
                        usernameEditText.setEnabled(false);

                        return;
                    }
                    checkUNProgressCircle.setVisibility(View.VISIBLE);
                    final String newUsername = usernameEditText.getText().toString();

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    service.changeUsername(userID, accessToken, newUsername).enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                            if (response != null && response.body() != null && response.body().getRespCode().equals("100")) {
                                username = newUsername;
                                Intent data = new Intent();
                                data.putExtra("access_token", accessToken);
                                data.putExtra("username", username);
                                setResult(RESULT_OK, data);

                                preferences.edit().putString("username", username).apply();

                                changeUsernameButton.setText("change");
                                usernameEditText.setEnabled(false);
                                uniqueUNImageView.setVisibility(View.INVISIBLE);
                                duplicateUNImageView.setVisibility(View.INVISIBLE);
                                checkUNProgressCircle.setVisibility(View.INVISIBLE);
                                changeUsernameButton.setEnabled(true);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Changing username failed", BaseTransientBottomBar.LENGTH_SHORT);
                            }
                            checkUNProgressCircle.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Changing username failed", BaseTransientBottomBar.LENGTH_SHORT).show();
                            checkUNProgressCircle.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeUsernameButton.setEnabled(false);
                if (timer != null) timer.cancel();
                duplicateUNImageView.setVisibility(View.INVISIBLE);
                uniqueUNImageView.setVisibility(View.INVISIBLE);
                if (s.length() <= 0) {
                    checkUNProgressCircle.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() <= 0) return;
                if (s.toString().equals(username)) {
                    uniqueUNImageView.setVisibility(View.VISIBLE);
                    duplicateUNImageView.setVisibility(View.INVISIBLE);
                    checkUNProgressCircle.setVisibility(View.INVISIBLE);
                    changeUsernameButton.setEnabled(true);
                    return;
                }
                if (checkUNProgressCircle.getVisibility() != View.VISIBLE) checkUNProgressCircle.setVisibility(View.VISIBLE);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uniqueUNImageView.setVisibility(View.INVISIBLE);
                            }
                        });
                        service.checkUsernameExists(s.toString()).enqueue(new Callback<ResponseCode>() {
                            @Override
                            public void onResponse(Call<ResponseCode> call, final Response<ResponseCode> response) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (response.body().getRespCode().equals("100")) {
                                            uniqueUNImageView.setVisibility(View.VISIBLE);
                                            changeUsernameButton.setEnabled(true);
                                        } else if (response.body().getRespCode().equals("2")) {
                                            duplicateUNImageView.setVisibility(View.VISIBLE);
                                            changeUsernameButton.setEnabled(false);
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_LONG).show();
                                            changeUsernameButton.setEnabled(false);
                                        }
                                        checkUNProgressCircle.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<ResponseCode> call, Throwable t) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkUNProgressCircle.setVisibility(View.INVISIBLE);
                                        changeUsernameButton.setEnabled(false);
                                    }
                                });
                            }
                        });
                    }
                }, DELAY);
            }
        });
        
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
                builder.setView(dialogView);

                final EditText oldPasswordEditText = dialogView.findViewById(R.id.old_password_edittext);
                final EditText newPasswordEditText = dialogView.findViewById(R.id.new_password_edittext);
                final EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password_edittext);
                final TextView errorTextView = dialogView.findViewById(R.id.error_label);

                builder.setPositiveButton("Change Password", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button changeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        changeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                errorTextView.setVisibility(View.INVISIBLE);
                                final String oldPassword = oldPasswordEditText.getText().toString();
                                if (oldPassword.isEmpty()) {
                                    errorTextView.setText("Fields empty");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    return;
                                }
                                final String newPassword = newPasswordEditText.getText().toString();
                                if (newPassword.isEmpty()) {
                                    errorTextView.setText("Fields empty");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    return;
                                }

                                if (!newPassword.equals(confirmPasswordEditText.getText().toString())) {
                                    errorTextView.setText("Passwords dont match");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    return;
                                }
                                if (newPassword.length() < 8) {
                                    errorTextView.setText("Password must be at least 8 characters");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    return;
                                }

                                service.getSalt(username).enqueue(new Callback<Salt>() {
                                    @Override
                                    public void onResponse(Call<Salt> call, Response<Salt> response) {
                                        Salt salt = response.body();

                                        if (salt.getRespCode() == null && !salt.getSalt().isEmpty()) {
                                            String saltString = salt.getSalt();
                                            byte[] saltBytes = Base64.decode(saltString.replace("-", "/"), Base64.NO_WRAP);
                                            byte[] passHashed = PasswordManager.hash(oldPassword.toCharArray(), saltBytes);
                                            String passHashedString = Base64.encodeToString(passHashed, Base64.NO_WRAP);
                                            passHashedString = passHashedString.replace("/", "-");

                                            byte[] newSaltBytes = PasswordManager.getNextSalt();
                                            byte[] newPassHashed = PasswordManager.hash(newPassword.toCharArray(), newSaltBytes);

                                            String newSaltString = Base64.encodeToString(newSaltBytes, Base64.NO_WRAP);
                                            String newPassHashedString = Base64.encodeToString(newPassHashed, Base64.NO_WRAP);

                                            newSaltString = newSaltString.replace('/', '-');
                                            newPassHashedString = newPassHashedString.replace('/','-');

                                            service.changePassword(userID, accessToken, passHashedString, newPassHashedString, newSaltString).enqueue(new Callback<ResponseCode>() {
                                                @Override
                                                public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                                                    if (response.body() != null && response.body().getRespCode().equals("100")) {
                                                        Intent data = new Intent();
                                                        accessToken = response.body().getRespMsg();
                                                        data.putExtra("access_token", accessToken);
                                                        data.putExtra("username", username);
                                                        setResult(RESULT_OK, data);

                                                        preferences.edit().putString("access_token", accessToken).apply();
                                                        dialog.dismiss();

                                                        View view = getCurrentFocus();
                                                        if (view != null) {
                                                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                        }
                                                    } else {
                                                        errorTextView.setText("Something went wrong; try again");
                                                        errorTextView.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseCode> call, Throwable t) {
                                                    errorTextView.setText("Something went wrong; try again");
                                                    errorTextView.setVisibility(View.VISIBLE);
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

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        editor.putBoolean("location_enabled", true);
                    } else {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_FINE_LOCATION_REQUEST);
                    }
                } else {
                    editor.putBoolean("location_enabled", false);

                    //Clearing location data off server
                    service.setLocation(userID, accessToken, "", "", "").enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {

                        }
                    });
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
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Clears the backstack of activities/fragments
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
                                                        editor.remove("location_enabled");
                                                        editor.apply();

                                                        //Start login activity
                                                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Clears the backstack of activities/fragments
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editor.putBoolean("location_enabled", true);
            } else {
                editor.putBoolean("location_enabled", false);
                locationSwitch.setChecked(false);
            }
            editor.apply();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        if (changeUsernameButton.getText().toString().equals("save")) {
            usernameEditText.setText(username);
            uniqueUNImageView.setVisibility(View.INVISIBLE);
            duplicateUNImageView.setVisibility(View.INVISIBLE);
            checkUNProgressCircle.setVisibility(View.INVISIBLE);
            changeUsernameButton.setEnabled(true);
            changeUsernameButton.setText("change");
            usernameEditText.setEnabled(false);
        } else {
            super.onBackPressed();
        }
    }
}
